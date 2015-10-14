'use strict';

angular.module('teammanagerApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


