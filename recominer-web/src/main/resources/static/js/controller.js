app.controller("projectController", ['$scope', '$log', '$window', '$http', '$mdSidenav', '$mdToast',
  function($scope, $log, $window, $http, $mdSidenav, $mdToast) {
    $scope.projects = null;
    $scope.commits = null;
    $scope.files = null;
    $scope.cochanges = null;
    $scope.activeProject = null;
    $scope.activeCommit = null;
    $scope.activeFile = null;
    $scope.tabIndex = 0;

    $log.debug("Fetching project...");
    $http.get("/projects")
        .then(function(response) {
                $scope.projects = response.data;
            },
            function(response) {
                $scope.projects = null;
            }
        );

    // Toolbar search toggle
    $scope.toggleSearchProjects = function(element) {
        $scope.showSearchProjects = !$scope.showSearchProjects;
    };
    $scope.toggleSearchCommits = function(element) {
        $scope.showSearchCommits = !$scope.showSearchCommits;
    };
    $scope.toggleSearchFiles = function(element) {
        $scope.showSearchFiles = !$scope.showSearchFiles;
    };

    // Sidenav toggle
    $scope.toggleSidenav = function(menuId) {
        $mdSidenav(menuId).toggle();
    };

    // Load commits
    $scope.getRecentCommitsOf = function(project) {
        $scope.activeProject = project;
        $scope.activeCommit = null;
        $scope.activeFile = null;
        $scope.cochanges = null;
        $log.debug("Fetching recent commits from project " + project.name + "...");
        $http.post("/commits", project)
            .then(function(response) {
                    $scope.commits = response.data;
                },
                function(response) {
                    $scope.commits = null;
                }
            );
        $scope.tabIndex = 1;
    };

    // Load files
    $scope.getFilesOf = function(commit) {
        $scope.activeCommit = commit;
        $scope.activeFile = null;
        $scope.cochanges = null;
        $log.debug("Fetching files from commit " + commit.revision + "...");
        commit.project = $scope.activeProject;
        $http.post("/files", commit)
            .then(function(response) {
                    $scope.files = response.data;
                },
                function(response) {
                    $scope.files = null;
                }
            );
        $scope.tabIndex = 2;
    };

    // Load cochanges
    $scope.getPredictedCochangesOf = function(file) {
        $scope.activeFile = file;
        $scope.cochanges = null;
        $log.debug("Fetching predicted cochanges for file " + file.name + "...");
        file.project = $scope.activeProject;
        file.commit = $scope.activeCommit;
        $http.post("/predictedCochanges", file)
            .then(function(response) {
                    $scope.cochanges = response.data;
                },
                function(response) {
                    $scope.cochanges = null;
                }
            );
    };

    // Load cochanges predicted by AR
    $scope.getArPredictedCochangesOf = function(file) {
        $scope.activeFile = file;
        $log.debug("Fetching predicted cochanges for file " + file.name + "...");
        file.project = $scope.activeProject;
        file.commit = $scope.activeCommit;
        $http.post("/arPredictedCochanges", file)
            .then(function(response) {
                    $scope.cochanges = response.data;
                },
                function(response) {
                    $scope.cochanges = null;
                }
            );
    };

    // Load cochanges predicted by ML
    $scope.getMlPredictedCochangesOf = function(file) {
        $scope.activeFile = file;
        $log.debug("Fetching predicted cochanges for file " + file.name + "...");
        file.project = $scope.activeProject;
        file.commit = $scope.activeCommit;
        $http.post("/mlPredictedCochanges", file)
            .then(function(response) {
                    $scope.cochanges = response.data;
                },
                function(response) {
                    $scope.cochanges = null;
                }
            );
    };

    // Submit feedback
    $scope.submitFeedback = function(cochange) {
        cochange.file.project = $scope.activeProject;
        $log.debug("Sending feedback of " + cochange.file.name + "...");
        $http.post("/saveFeedback", cochange)
            .then(function(response) {
                    $mdToast.show($mdToast.simple().position('bottom right').textContent(response.data.message));
                },
                function(response) {
                    $mdToast.show($mdToast.simple().position('bottom right').textContent("Failed to submit feedback! " + response.data.message));
                }
            );
    };

}]);
