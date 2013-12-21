'use strict';

// Declare app level module which depends on others
angular.module('app', ['ngResource'])
  .constant('apiUrl', 'http://localhost:9000\:9000/api/applicants')
  .config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {templateUrl: '/views/main', controller: 'ListCtrl'})
      .when('/create', {templateUrl: 'views/detail', controller: 'CreateCtrl'})
      .otherwise({redirectTo: '/'});
    $locationProvider.html5Mode(true);
  }])

// list applicants and display entry form
.controller('ListCtrl', ['$scope', '$resource', 'apiUrl', function($scope, $resource, apiUrl) {
  $scope.applicants = $resource(apiUrl).query();
}])

// create applicant form
.controller('CreateCtrl', ['$scope', '$resource', 'apiUrl', '$location', function($scope, $resource, apiUrl, $location) {
  // save new applicant
  $scope.save = function() {
    $resource(apiUrl).save($scope.applicant);
	$location.path('/');
  };
}]);