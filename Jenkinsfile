@Library('libpipelines@feature/multibranch') _

hose {
    EMAIL = 'qa'
    MODULE = 'common-utils'
    DEVTIMEOUT = 30
    RELEASETIMEOUT = 30
    FOSS = true
    REPOSITORY = 'common-utils'
    
    DEV = { config ->
    
        doCompile(config)
        doUT(config)
        doPackage(config)

        parallel(DOC: {
            doDoc(config)
        }, QC: {
            doStaticAnalysis(config)
        }, DEPLOY: {
            doDeploy(config)
        }, failFast: config.FAILFAST)

    }
}

