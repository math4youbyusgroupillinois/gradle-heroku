package org.ratpackframework.gradle
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.transport.RefSpec
import org.gradle.api.GradleException

class HerokuAppDeployTask extends HerokuTask {

    static final String AUTHOR_NAME = 'Gradle Heroku plugin'
    static final String AUTHOR_EMAIL = 'invalid@example.org'
    static final String COMMIT_MESSAGE = "Commit generated artifact for heroku deployment."
    static final String DEPLOYABLE_ARTIFACT_FOLDER = "build/libs"
    static final String REF_SPEC = "master:master"

    HerokuAppDeployTask(){
        super("Deploy the application to Heroku.")
    }

    @Override
    void execute(Object params) {
        def jarFolder = DEPLOYABLE_ARTIFACT_FOLDER as File
        if(!jarFolder.exists()){
            throw new GradleException('No deployable artifacts found. First run: $ gradle build')
        }

        def config = git.repository.config
        def remotes = config.getSubsections("remote")
        if(!remotes.contains(REMOTE_NAME)){
            throw new GradleException("No remote $REMOTE_NAME found. First run: \$ gradle herokuAppCreate")
        }

        logger.quiet "\nAdding deployable artifacts in $DEPLOYABLE_ARTIFACT_FOLDER to git repo."
        git.add()
            .addFilepattern(DEPLOYABLE_ARTIFACT_FOLDER)
            .call()

        logger.quiet "Committing to git."
        def author = new PersonIdent(AUTHOR_NAME, AUTHOR_EMAIL)
        git.commit()
            .setMessage(COMMIT_MESSAGE)
            .setAuthor(author)
            .call()

        logger.quiet "Pushing to remote repo: $REMOTE_NAME."
        logger.quiet "This could take a while... Really."
        def refSpec = new RefSpec(REF_SPEC)
        git.push()
            .setRemote(REMOTE_NAME)
            .setRefSpecs(refSpec)
            .call()
        logger.quiet "Finished pushing to: $REMOTE_NAME!"
    }
}