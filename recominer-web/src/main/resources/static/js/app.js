var app = angular.module('app', ['ngRoute', 'ngResource', 'ngMaterial']);
app.config(function ($routeProvider) {
    $routeProvider
            .when('/', {
                templateUrl: '/views/project.html',
                controller: 'projectController'
            })
            .otherwise(
                    {redirectTo: '/'}
            );
});

app.config(function ($mdThemingProvider) {
    var customBlueMap = $mdThemingProvider.extendPalette('indigo', {
        'contrastDefaultColor': 'light',
        'contrastDarkColors': ['50'],
        '50': 'ffffff'
    });
    $mdThemingProvider.definePalette('customBlue', customBlueMap);
    $mdThemingProvider.theme('default')
            .primaryPalette('customBlue', {
                'default': '500',
                'hue-1': '50'
            })
            .accentPalette('pink');
    $mdThemingProvider.theme('input', 'default')
            .primaryPalette('grey')
});

app.config(function ($mdIconProvider) {
    $mdIconProvider
            // linking to https://github.com/google/material-design-icons/tree/master/sprites/svg-sprite
            //
            .iconSet('action', '/svg/svg-sprite-action.svg', 24)
            .iconSet('alert', '/svg/svg-sprite-alert.svg', 24)
            .iconSet('av', '/svg/svg-sprite-av.svg', 24)
            .iconSet('communication', '/svg/svg-sprite-communication.svg', 24)
            .iconSet('content', '/svg/svg-sprite-content.svg', 24)
            .iconSet('device', '/svg/svg-sprite-device.svg', 24)
            .iconSet('editor', '/svg/svg-sprite-editor.svg', 24)
            .iconSet('file', '/svg/svg-sprite-file.svg', 24)
            .iconSet('hardware', '/svg/svg-sprite-hardware.svg', 24)
            .iconSet('image', '/svg/svg-sprite-image.svg', 24)
            .iconSet('maps', '/svg/svg-sprite-maps.svg', 24)
            .iconSet('navigation', '/svg/svg-sprite-navigation.svg', 24)
            .iconSet('notification', '/svg/svg-sprite-notification.svg', 24)
            .iconSet('social', '/svg/svg-sprite-social.svg', 24)
            .iconSet('toggle', '/svg/svg-sprite-toggle.svg', 24)

            // Illustrated user icons used in the docs https://material.angularjs.org/latest/#/demo/material.components.gridList
            .iconSet('avatars', '/svg/avatar-icons.svg', 24)
            .defaultIconSet('/svg/svg-sprite-action.svg', 24);
});
