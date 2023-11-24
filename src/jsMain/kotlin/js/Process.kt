package js

external val process: Process

external interface Process {

    val env: ProcessEnvVariables

}

external interface ProcessEnvVariables {

    val WEB_SOCKET_PROTOCOL: String

}