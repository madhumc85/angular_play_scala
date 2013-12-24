'use strict';

// Declare app level module which depends on others
angular.module('app', ['ngResource', 'ngRoute'])
  .constant('apiUrl', 'http://localhost:9000/api')
  .config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {templateUrl: '/views/main', controller: 'ListCtrl'})
      .when('/create', {templateUrl: '/views/detail', controller: 'CreateCtrl'})
      .when('/profile', {templateUrl: '/views/profile', controller: 'ProfileCtrl'})
      .when('/edit/:id', {templateUrl: '/views/detail', controller: 'EditCtrl'})
      .otherwise({redirectTo: '/'});
    $locationProvider.html5Mode(true).hashPrefix('!');
  }])

// home page list forms
.controller('ListCtrl', ['$scope', '$resource', 'apiUrl', function($scope, $resource, apiUrl) {
  $scope.forms = $resource(apiUrl+'/forms').query();
}])

// create profile form
.controller('CreateCtrl', ['$scope', '$resource', 'apiUrl', '$location', function($scope, $resource, apiUrl, $location) {
  $scope.cancel = function() {
    $location.path('/');
  };
  // save new profile
  $scope.save = function() {
    $resource(apiUrl+'/profiles').save($scope.profile);
	  $location.path('/profile');
  };
}])

// display profile
.controller('ProfileCtrl', ['$scope', '$resource', 'apiUrl', '$location', '$anchorScroll', function($scope, $resource, apiUrl, $location, $anchorScroll) {
  $scope.forms = $resource(apiUrl+'/forms').query();
  $scope.profiles = $resource(apiUrl+'/profiles').query();
}])

// edit profile form
.controller('EditCtrl', ['$scope', '$resource', '$routeParams', 'apiUrl', '$location', '$anchorScroll', function($scope, $resource, $routeParams, apiUrl, $location, $anchorScroll) {
  if ($routeParams.id) {
    $scope.profile = $resource(apiUrl+'/profiles/'+$routeParams.id).get();
  }

  $scope.cancel = function() {
    $location.hash('top');
    $anchorScroll();
    $location.path('/profile');
  };

  // save profile update
  $scope.save = function() {
    $resource(apiUrl+'/profiles/'+$routeParams.id).save($scope.profile);
    $scope.$apply();
    $location.hash('top');
    $anchorScroll();
    $location.path('/profile');
  };
}]);