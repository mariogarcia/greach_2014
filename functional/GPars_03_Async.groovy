@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

final File NBA_SCORES_FILE = new File('data/nbascore.csv')
final String COMMA = ','

def benchmarkAsync = benchmark(warmUpTime: 10) {
    'A' {

    }
    'B' {

    }
} // END OF BENCHMARK


benchmarkAsync.prettyPrint()
