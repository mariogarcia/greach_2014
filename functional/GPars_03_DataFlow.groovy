@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')
@Grab(group='org.codehaus.gpars', module='gpars', version='0.11')

import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.dataflow.Dataflow.task

import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.dataflow.DataflowQueue

final File NBA_SCORES_FILE = new File('nba/nbascore.csv')
final String COMMA = ','

def sequentialVsParallel = benchmark {

    'Sequencial Groovy' {

    }

    'Dataflow Groovy' {

    }
} // END OF BENCHMARK


sequentialVsParallel.prettyPrint()
