 'use strict';

angular.module('teammanagerApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-teammanagerApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-teammanagerApp-params')});
                }
                return response;
            }
        };
    });
