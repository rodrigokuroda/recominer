var app = angular.module('app', ['ngRoute','ngResource']);
app.config(function($routeProvider){
    $routeProvider
        .when('/',{
            templateUrl: '/views/password.html',
            controller: 'passwordController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});

