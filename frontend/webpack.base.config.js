const ScalaJS = require('./scalajs.webpack.config');
const { merge } = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');
const rootDir = path.resolve(__dirname, '../../../../src/main/resources');
const cssDir = path.resolve(rootDir, 'css');

const WebApp = merge(ScalaJS, {
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
        test: /\.(woff|woff2)$/,
        type: 'asset/inline', // exports a data URI of the asset
        include: [
          path.resolve(rootDir, 'fonts')
        ]
      },
      {
        test: /\.(png|woff|woff2|eot|ttf|svg)$/,
        type: 'asset/inline',
        include: [
          rootDir
        ]
      },
      {
        test: /\.(png|svg|jpg|jpeg)$/,
        type: 'asset/resource',
        include: [
          rootDir
        ],
        generator: {
          filename: 'img/[name].[hash][ext]'
        }
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
  }
});

module.exports = WebApp;
