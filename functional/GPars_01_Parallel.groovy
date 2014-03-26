@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.GParsPool.withPool

final File NBA_SCORES_FILE = new File('data/nbascore.csv')
final String COMMA = ','

// GATHERING DATA
def field = { field ->
    return { line -> line.split(COMMA).getAt(field) }
}

def fields = { Integer... fieldNumbers ->
    return { line -> fieldNumbers.collect{ fieldNumber -> field(fieldNumber) << line } }
}

// TRANSFORMING DATA
def toInteger = { possibleNumbers -> possibleNumbers*.toInteger() }
def sort = { numbers -> numbers.sort() }
def substract = { a, b -> b - a }
def differenceCollector = { line ->
    substract << sort << toInteger << fields(2,4) << line
}

// SAFETY HELPER
def safely = { closure ->
    return { line -> try { closure(line) } catch(e) { 0 } }
}

// TEMPLATE METHOD
def collectMaximumDifference = { strategy ->
    return NBA_SCORES_FILE.withReader(strategy)
}

def sequentialVsParallel = benchmark {
    'Sequencial Groovy' {
        Integer result = collectMaximumDifference { reader ->
            return reader.collect(safely(differenceCollector)).max()
        }

        assert result == 68
    }
    'Parallel Groovy' {
        Integer result = collectMaximumDifference { reader ->
            return withPool(4) { reader.collectParallel(safely(differenceCollector)).max() }
        }

        assert result == 68
    }
} // END OF BENCHMARK


sequentialVsParallel.prettyPrint()
