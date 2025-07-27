const path = require('path');
const HtmlWebpackPlugin = require("html-webpack-plugin");
const DotenvPlugin = require('dotenv-webpack');

const htmlWebpackPlugin = new HtmlWebpackPlugin({
    template: path.join(__dirname, "index.html"),
    filename: "index.html",
});

const dotenvPlugin = new DotenvPlugin({
    path: path.join(__dirname, ".env")
});

module.exports = {
    mode: "development",

    entry: {
        coupon: path.join(__dirname, "../index.js"),
    },
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, '../dist'),
        clean: true
    },
    devServer: {
        setupMiddlewares: (middlewares, devServer) => {
            if (!devServer) {
                throw new Error("webpack-dev-server is not defined");
            }

            devServer.app.get("/some/path", (_, response) => {
                response.send("some path get");
            })

            return middlewares;
        },

        open: {
            target: ['/index.html'],
            app: {
                "name": 'google-chrome'
            },
        },
        compress: true,
        port: 9000,
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                use: [
                    {
                        loader: 'babel-loader',
                        options: {
                            "presets": ["@babel/preset-react"]
                        }
                    }
                ],
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"]
            },
            {
                test: /\.svg$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: "babel-loader"
                    },
                    {
                        loader: "react-svg-loader",
                        options: {
                            jsx: true
                        }
                    }
                ]
            }
        ]
    },
    plugins: [dotenvPlugin, htmlWebpackPlugin],
    resolve: {
        extensions: [".js", ".jsx"]
    }
};