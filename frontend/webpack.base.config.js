const ScalaJS = require('./scalajs.webpack.config');
const Merge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');
const rootDir = path.resolve(__dirname, '../../../../src/main/resources');
const cssDir = path.resolve(rootDir, 'css');
// const vendorsDir = path.resolve(rootDir, 'vendors');

const WebApp = Merge(ScalaJS, {
  entry: {
    styles: [path.resolve(cssDir, './www.js')],
    vendors: [path.resolve(cssDir, './vendors.js')],
    fonts: [path.resolve(cssDir, './fonts.js')]
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          { loader: 'css-loader', options: { importLoaders: 1, url: true } },
          'postcss-loader'
        ]
      },
      {
        test: /\.(png|woff|woff2|eot|ttf|svg)$/,
        use: [
          { loader: 'url-loader' }
        ]
      },
      {
        test: /\.less$/,
        use: [
          MiniCssExtractPlugin.loader,
          { loader: 'css-loader', options: { importLoaders: 1 } },
          'postcss-loader',
          'less-loader'
        ]
      }
    ]
  },
  // output: {
  //   filename: '[name].js'
  // },
  // plugins: [
  //   new MiniCssExtractPlugin({filename: '[name].css'})
  // ]
});

module.exports = WebApp;
