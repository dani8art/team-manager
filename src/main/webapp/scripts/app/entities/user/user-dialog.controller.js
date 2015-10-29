'use strict';

angular.module('teammanagerApp').controller('UserDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'User', 'Entrenamiento', 'Manytomany', 'Manytomanynoowner', 'Manytoone', 'Onetoone',
        function($scope, $stateParams, $modalInstance, entity, User, Entrenamiento, Manytomany, Manytomanynoowner, Manytoone, Onetoone) {

        $scope.user = entity;
        $scope.entrenamientos = Entrenamiento.query();
        $scope.manytomanys = Manytomany.query();
        $scope.manytomanynoowners = Manytomanynoowner.query();
        $scope.manytoones = Manytoone.query();
        $scope.onetoones = Onetoone.query();
        $scope.load = function(id) {
            User.get({id : id}, function(result) {
                $scope.user = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('teammanagerApp:userUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.user.id != null) {
                User.update($scope.user, onSaveFinished);
            } else {
                User.save($scope.user, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.abbreviate = function (text) {
            if (!angular.isString(text)) {
                return '';
            }
            if (text.length < 30) {
                return text;
            }
            return text ? (text.substring(0, 15) + '...' + text.slice(-10)) : '';
        };

        $scope.byteSize = function (base64String) {
            if (!angular.isString(base64String)) {
                return '';
            }
            function endsWith(suffix, str) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            }
            function paddingSize(base64String) {
                if (endsWith('==', base64String)) {
                    return 2;
                }
                if (endsWith('=', base64String)) {
                    return 1;
                }
                return 0;
            }
            function size(base64String) {
                return base64String.length / 4 * 3 - paddingSize(base64String);
            }
            function formatAsBytes(size) {
                return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + " bytes";
            }

            return formatAsBytes(size(base64String));
        };

        $scope.setDescription = function ($file, user) {
            if ($file && $file.$error == 'pattern') {
                return;
            }
            if ($file) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($file);
                fileReader.onload = function (e) {
                    var base64Data = e.target.result.substr(e.target.result.indexOf('base64,') + 'base64,'.length);
                    $scope.$apply(function() {
                        user.description = base64Data;
                        user.descriptionContentType = $file.type;
                    });
                };
            }
        };
}]);
