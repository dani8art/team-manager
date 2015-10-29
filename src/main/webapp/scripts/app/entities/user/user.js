'use strict';

angular.module('teammanagerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('user', {
                parent: 'entity',
                url: '/users',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Users'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/user/users.html',
                        controller: 'UserController'
                    }
                },
                resolve: {
                }
            })
            .state('user.detail', {
                parent: 'entity',
                url: '/user/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'User'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/user/user-detail.html',
                        controller: 'UserDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'User', function($stateParams, User) {
                        return User.get({id : $stateParams.id});
                    }]
                }
            })
            .state('user.new', {
                parent: 'user',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/user/user-dialog.html',
                        controller: 'UserDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    username: null,
                                    password: null,
                                    email: null,
                                    edad: null,
                                    fechaNacimiento: null,
                                    description: null,
                                    descriptionContentType: null,
                                    role: null,
                                    posision: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('user', null, { reload: true });
                    }, function() {
                        $state.go('user');
                    })
                }]
            })
            .state('user.edit', {
                parent: 'user',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/user/user-dialog.html',
                        controller: 'UserDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['User', function(User) {
                                return User.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('user', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
