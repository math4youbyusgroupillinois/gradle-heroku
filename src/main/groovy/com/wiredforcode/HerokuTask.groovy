package com.wiredforcode

import com.heroku.api.HerokuAPI
import org.eclipse.jgit.api.InitCommand
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class HerokuTask extends DefaultTask {

    static final String REMOTE_NAME = "heroku"

    HerokuAPI herokuAPI
    def git

    HerokuTask(){}

    HerokuTask(String description){
        this.description = description
    }

    @TaskAction
    void start(){
        prepareAPI()
        prepareRepo()
        execute(project.heroku)
    }

    private void prepareRepo() {
        def command = new InitCommand()
        command.directory = "." as File
        git = command.call()
    }

    private void prepareAPI() {
        def apiKey = "${project.heroku.apiKey}"
        herokuAPI = new HerokuAPI(apiKey)
    }

    abstract void execute(params)

}
