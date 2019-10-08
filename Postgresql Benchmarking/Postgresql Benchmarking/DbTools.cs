using Dapper;
using FirebirdSql.Data.FirebirdClient;
using Npgsql;
using System.Collections.Generic;
using System.Data.Common;
using System.IO;
using System.Linq;

namespace Postgresql.Benchmarking
{
    public static class DbTools
    {
        private static FbConnectionStringBuilder PrepareFirebirdConnectionString(string connectionString)
        {
            var appDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);

            var csb = new FbConnectionStringBuilder(connectionString);
            var clientLibFilename = connectionString.ToLowerInvariant().Contains("clientlibrary")
                ? Path.GetFileName(csb.ClientLibrary)
                : "fbclient.dll";

            csb.ClientLibrary = Path.Combine(appDir, clientLibFilename);
            if (!connectionString.ToLowerInvariant().Contains("servertype"))
                csb.ServerType = FbServerType.Embedded;

            if (string.IsNullOrWhiteSpace(csb.UserID))
                csb.UserID = "SYSDBA";

            if (!connectionString.ToLowerInvariant().Contains("charset"))
                csb.Charset = "UTF8";

            return csb;
        }
        
        public static DbConnection NewConnection(DbEngineType engineType)
        {
            var csb = PrepareFirebirdConnectionString(Config.DbConnectionString);
            return engineType == DbEngineType.Postgres
                ? (DbConnection) new NpgsqlConnection(Config.DbConnectionString)
                : (DbConnection) new FbConnection(csb.ConnectionString);
        }

        private static DbCommand FbCommand(string sql, DbConnection conn)
        {
            return new FbCommand(sql, conn as FbConnection);
        }

        private static DbParameter FbParam(string name, object value)
        {
            return new FbParameter(name, value);
        }
       
        private static DbCommand PostgresCommand (string sql, DbConnection conn)
        {
            return new NpgsqlCommand(sql, conn as NpgsqlConnection);
        }

        private static DbParameter PostgresParam(string name, object value)
        {
            return new NpgsqlParameter(name, value);
        }

        private static DbCommand CreateCommand(DbEngineType dbEngineType, string sql, DbConnection conn)
        {
            return dbEngineType == DbEngineType.Postgres
                ? PostgresCommand(sql, conn)
                : FbCommand(sql, conn);
        }

        private static DbParameter CreateParameter(DbEngineType dbEngineType, string name, object value)
        {
            return dbEngineType == DbEngineType.Postgres
                ? PostgresParam(name, value)
                : FbParam(name, value);
        }

        public static int Execute(DbEngineType dbEngineType, string sql, Dictionary<string, object> parameters)
        {
            using (var conn = NewConnection(dbEngineType))
            {
                try
                {
                    var cmd = CreateCommand(dbEngineType, sql, conn);
                    foreach (var param in parameters)
                    {
                        cmd.Parameters.Add(CreateParameter(dbEngineType, param.Key, param.Value));
                    }

                    conn.Open();
                    return cmd.ExecuteNonQuery();
                }
                finally
                {
                    conn.Close();
                }
            }
        }

        public static List<T> Query<T>(DbEngineType dbEngineType, string sql, Dictionary<string, object> parameters)
        {
            Dapper.DefaultTypeMap.MatchNamesWithUnderscores = true;
            using (var conn = NewConnection(dbEngineType))
            {
                try
                {
                    conn.Open();

                    var query = parameters.Any()
                        ? conn.Query<T>(sql, parameters)
                        : conn.Query<T>(sql);

                    return query.ToList();
                }
                finally
                {
                    conn.Close();
                }
            }
        }

        public static void InitFirebird(string connectionString)
        {
            var csb = PrepareFirebirdConnectionString(connectionString);
            var dbFile = csb.Database;
            if (!File.Exists(dbFile))
                FbConnection.CreateDatabase(csb.ConnectionString, false);

            var sql = "CREATE TABLE test (test_key varchar(100) NOT NULL, test_value varchar(1000) not NULL, CONSTRAINT test__pk PRIMARY KEY (test_key));";
            Execute(DbEngineType.Firebird, sql, new Dictionary<string, object>());
        }       
    }
}
