var app = angular.module("app", ["ngResource"])
	.constant("apiUrl", "http://localhost:9000\:9000/api") // to tell AngularJS that 9000 is not a dynamic parameter
	.config(["$routeProvider", function($routeProvider) {
		return $routeProvider.when("/", {
			templateUrl: "/views/main",
			controller: "ListCtrl"
		}).when("/create", {
			templateUrl: "/views/detail",
			controller: "CreateCtrl"
	    }).otherwise({
			redirectTo: "/"
		});
	}
	]).config([
	"$locationProvider", function($locationProvider) {
		return $locationProvider.html5Mode(true).hashPrefix("!");
	}
]);

// the global controller
app.controller("AppCtrl", ["$scope", "$location", function($scope, $location) {
	// the very sweet go function is inherited to all other controllers
	$scope.go = function (path) {
		$location.path(path);
	};
}]);

// the list controller
app.controller("ListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
	var Applicants = $resource(apiUrl + "/applicants"); // a RESTful-capable resource object
	$scope.applicants = Applicants.query(); // for the list of applicants in public/html/main.html
}]);

// the create controller
app.controller("CreateCtrl", ["$scope", "$resource", "$timeout", "apiUrl", function($scope, $resource, $timeout, apiUrl) {
	// to save an applicant
	$scope.save = function() {
		var CreateApplicant = $resource(apiUrl + "/applicants"); // a RESTful-capable resource object
		CreateApplicant.save($scope.applicant); // $scope.applicant comes from the detailForm in public/html/detail.html
		$timeout(function() { $scope.go('/'); }); // go back to public/html/main.html
	};
}]);