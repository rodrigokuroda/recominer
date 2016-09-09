app.controller("passwordController", ['$scope', '$log', '$window', '$http', function($scope, $log, $window, $http) {

	$log.debug("Test");
	$scope.password = null;
	$scope.score = 0;
	$scope.scoreLabel = "Muito Fraco";

	$scope.evaluatePassword = function() {
		
		$log.debug("Evaluating password...");

		$http.post("/evaluate", $scope.password)
			.then(function(response) {
				$scope.score = response.data;
	        }, 
	        function(response) { // optional
	        	$scope.score = 0;
	        }
	    );
		
	};
	
	$scope.scoreLabel = function() {
		if ($scope.score >= 0 && $scope.score < 20) { return "Muito Fraco"; }
		else if ($scope.score >= 20 && $scope.score < 40) { return "Fraco"; }
		else if ($scope.score >= 40 && $scope.score < 60) { return "Bom"; }
		else if ($scope.score >= 60 && $scope.score < 80) { return "Forte"; }
		else if ($scope.score >= 80 && $scope.score <= 100) { return "Muito Forte"; }
	}
	
	$scope.classForScoreLabel = function() {
		if ($scope.score >= 0 && $scope.score < 20) { return "label label-danger"; }
		else if ($scope.score >= 20 && $scope.score < 40) { return "label label-warning"; }
		else if ($scope.score >= 40 && $scope.score < 60) { return "label label-info"; }
		else if ($scope.score >= 60 && $scope.score < 80) { return "label label-success"; }
		else if ($scope.score >= 80 && $scope.score <= 100) { return "label label-primary"; }
	}

}]);