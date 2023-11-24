module.exports = env => {
    const webpack = require("webpack");

    var webSocketProtocol;

    if (env.targetEnvironment === "PROD") {
        webSocketProtocol = "wss";
    } else {
        webSocketProtocol = "ws";
    }

    const environmentPlugin = new webpack.EnvironmentPlugin({
        WEB_SOCKET_PROTOCOL: webSocketProtocol
    });

    config.plugins.push(environmentPlugin);

    return config;
}