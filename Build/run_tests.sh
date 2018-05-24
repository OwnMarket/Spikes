SOLUTION_DIR=$1
BUILD_CONFIG=$2
OUTPUT_DIR=$3
TEST_RESULTS_DIR=$4
TESTS_FILTER=$5
TEST_PROJECTS=`find $SOLUTION_DIR/* -name $TESTS_FILTER -type d`

for TEST_PROJ in $TEST_PROJECTS 
do 
	TEST_RESULTS=`basename $TEST_PROJ`
	dotnet test $TEST_PROJ -c $BUILD_CONFIG -o $OUTPUT_DIR -r $TEST_RESULTS_DIR -l "trx;LogFileName=$TEST_RESULTS.trx" --no-build
done