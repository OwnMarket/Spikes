using System;
using System.Collections.Generic;

namespace Postgresql.Benchmarking
{
    public static class Db
    {
        public static object GetValue(DbEngineType dbEngineType, string key)
        {
            var sql =
                "select test_value from test where test_key = @test_key";
            var parameters =
                new Dictionary<string, object>() { { "@test_key", key } };

            return DbTools.Query<object>(dbEngineType, sql, parameters);
        }

        public static void SetValue(DbEngineType dbEngineType, string key, object value)
        {
            var sql =
                "insert into test (test_key, test_value) values (@test_key, @test_value)";
            var parameters =
                new Dictionary<string, object>
                {
                    {"@test_key", key },
                    {"@test_value", value }
                };

            try
            {
                DbTools.Execute(dbEngineType, sql, parameters);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}
