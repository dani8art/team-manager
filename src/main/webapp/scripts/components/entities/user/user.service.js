'use strict';

angular.module('teammanagerApp')
    .factory('User', function ($resource, DateUtils) {
        return $resource('api/users/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.fechaNacimiento = DateUtils.convertDateTimeFromServer(data.fechaNacimiento);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
