app.controller("projectController", ['$scope', '$log', '$window', '$http', '$mdSidenav', '$mdToast', '$mdDialog', '$sce',
    function($scope, $log, $window, $http, $mdSidenav, $mdToast, $mdDialog, $sce) {
        $scope.projects = [];
        $scope.issues = [];
        $scope.commits = [];
        $scope.files = [];
        $scope.cochanges = [];
        $scope.paginatedCochanges = [];
        $scope.activeProject = null;
        $scope.activeIssue = null;
        $scope.activeCommit = null;
        $scope.activeFile = null;
        $scope.tabIndex = 0;
        $scope.loadingProjects = false;
        $scope.loadingIssues = false;
        $scope.loadingCommits = false;
        $scope.loadingFiles = false;
        $scope.loadingCochanges = false;
        $scope.submitting = false;

        $scope.numPerPage = 250;
        $scope.paging = {
            total: 1,
            current: 1,
            onPageChanged: loadPages,
        };

        function loadPages() {
            var begin = (($scope.paging.current - 1) * $scope.numPerPage),
                end = begin + $scope.numPerPage;

            $scope.paginatedCochanges = $scope.cochanges.slice(begin, end);
        }

        function calculatePages() {
            $scope.paging.current = 1;
            $scope.paging.total = Math.ceil($scope.cochanges.length / $scope.numPerPage);
        }

        function resetPagination() {
            $scope.paging = {
                total: 1,
                current: 1,
                onPageChanged: loadPages,
            };
            $scope.paginatedCochanges = [];
        }

        $log.debug("Fetching project...");
        $scope.loadingProjects = true;
        $http.get("/projects")
            .then(function(response) {
                    $scope.projects = response.data;
                    $scope.loadingProjects = false;
                },
                function(response) {
                    $scope.projects = [];
                    $scope.loadingProjects = false;
                }
            );

        // Toolbar search toggle
        $scope.toggleSearchProjects = function(element) {
            $scope.showSearchProjects = !$scope.showSearchProjects;
        };
        $scope.toggleSearchIssues = function(element) {
            $scope.showSearchIssues = !$scope.showSearchIssues;
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

        // Load issues
        $scope.getIssuesOf = function(project, technique) {
            $scope.loadingIssues = true;
            $scope.activeProject = project;
            $scope.activeProject.technique = technique;
            $scope.activeIssue = null;
            $scope.activeCommit = null;
            $scope.activeFile = null;
            $scope.cochanges = [];
            resetPagination();
            $log.debug("Fetching opened issues from project " + project.name + "...");
            $http.post("/issues", project)
                .then(function(response) {
                        $scope.issues = response.data;
                        $scope.loadingIssues = false;
                    },
                    function(response) {
                        $scope.issues = [];
                        $scope.loadingIssues = false;
                    }
                );
            $scope.tabIndex = 1;
        };

        // Load commits
        $scope.getCommitsOf = function(issue, technique) {
            $scope.loadingCommits = true;
            $scope.activeIssue = issue;
            $scope.activeCommit = null;
            $scope.activeFile = null;
            $scope.cochanges = [];
            resetPagination();
            $log.debug("Fetching commits from issue " + issue.key + "...");
            issue.project = $scope.activeProject;
            $http.post("/commits", issue)
                .then(function(response) {
                        $scope.commits = response.data;
                        $scope.loadingCommits = false;
                        $scope.activeCommit = $scope.commits[0];
                        $scope.getFilesOf($scope.activeCommit, technique);
                    },
                    function(response) {
                        $scope.commits = [];
                        $scope.loadingCommits = false;
                    }
                );
        };

        // Load files
        $scope.getFilesOf = function(commit, technique) {
            $scope.loadingFiles = false;
            $scope.activeCommit = commit;
            $scope.activeFile = null;
            $scope.cochanges = [];
            resetPagination();
            $log.debug("Fetching files from commit " + commit.revision + "...");
            commit.project = $scope.activeProject;
            $http.post("/files", commit)
                .then(function(response) {
                        $scope.files = response.data;
                        $scope.loadingFiles = false;
                        $scope.activeFile = $scope.files[0];
                        if (technique == 'ML') {
                            $scope.getMlPredictedCochangesOf($scope.activeFile, technique);
                        } else if (technique == 'AR') {
                            $scope.getArPredictedCochangesOf($scope.activeFile, technique);
                        } else {
                            $scope.getCochangesOf($scope.activeFile, technique);
                        }
                    },
                    function(response) {
                        $scope.files = [];
                        $scope.loadingFiles = false;
                    }
                );
        };

        // Load cochanges
        $scope.getPredictedCochangesOf = function(file) {
            $scope.loadingCochanges = true;
            $scope.activeFile = file;
            $log.debug("Fetching predicted cochanges for file " + file.name + "...");
            file.project = $scope.activeProject;
            file.commit = $scope.activeCommit;
            $http.post("/predictedCochanges", file)
                .then(function(response) {
                        $scope.cochanges = response.data;
                        $scope.loadingCochanges = false;
                        resetPagination();
                        loadPages();
                        calculatePages();
                    },
                    function(response) {
                        $scope.cochanges = [];
                        $scope.loadingCochanges = false;
                    }
                );
        };

        // Load cochanges predicted by AR
        $scope.getArPredictedCochangesOf = function(file) {
            $scope.loadingCochanges = true;
            $scope.activeFile = file;
            $log.debug("Fetching predicted cochanges for file " + file.name + "...");
            file.project = $scope.activeProject;
            file.commit = $scope.activeCommit;
            $http.post("/arPredictedCochanges", file)
                .then(function(response) {
                        $scope.cochanges = response.data;
                        $scope.loadingCochanges = false;
                        resetPagination();
                        loadPages();
                        calculatePages();
                    },
                    function(response) {
                        $scope.cochanges = [];
                        $scope.loadingCochanges = false;
                    }
                );
        };

        // Load cochanges predicted by ML
        $scope.getMlPredictedCochangesOf = function(file) {
            $scope.loadingCochanges = true;
            $scope.activeFile = file;
            $log.debug("Fetching predicted cochanges for file " + file.name + "...");
            file.project = $scope.activeProject;
            file.commit = $scope.activeCommit;
            $http.post("/mlPredictedCochanges", file)
                .then(function(response) {
                        $scope.cochanges = response.data;
                        $scope.loadingCochanges = false;
                        resetPagination();
                        loadPages();
                        calculatePages();
                    },
                    function(response) {
                        $scope.cochanges = [];
                        $scope.loadingCochanges = false;
                    }
                );
        };

        // Load cochanges predicted by ML
        $scope.getCochangesOf = function(file) {
            $scope.loadingCochanges = true;
            $scope.activeFile = file;
            $log.debug("Fetching predicted cochanges for file " + file.name + "...");
            file.project = $scope.activeProject;
            file.commit = $scope.activeCommit;
            $http.post("/allFiles", file)
                .then(function(response) {
                        $scope.cochanges = response.data;
                        $scope.loadingCochanges = false;
                        resetPagination();
                        loadPages();
                        calculatePages();
                    },
                    function(response) {
                        $scope.cochanges = [];
                        $scope.loadingCochanges = false;
                    }
                );
        };

        // Submit feedback
        $scope.submitArFeedback = function() {
            $scope.submitting = true;
            $scope.activeIssue.project = $scope.activeProject;
            $scope.activeIssue.feedback.cochanges = $scope.cochanges;
            $log.debug("Sending feedback of " + $scope.activeIssue.key + "...");
            $http.post("/saveArFeedback", $scope.activeIssue)
                .then(function(response) {
                        $mdToast.show($mdToast.simple().position('bottom right').textContent(response.data.message));
                        $scope.submitting = false;
                    },
                    function(response) {
                        $mdToast.show($mdToast.simple().position('bottom right').textContent("Ocorreu um erro ao enviar seu feedback! Por favor, tente novamente." + response.data.message));
                        $scope.submitting = false;
                    }
                );
        };

        $scope.submitMlFeedback = function() {
            $scope.submitting = true;
            $scope.activeIssue.project = $scope.activeProject;
            $scope.activeIssue.feedback.cochanges = $scope.cochanges;
            $log.debug("Sending feedback of " + $scope.activeIssue.key + "...");
            $http.post("/saveMlFeedback", $scope.activeIssue)
                .then(function(response) {
                        $mdToast.show($mdToast.simple().position('bottom right').textContent(response.data.message));
                        $scope.submitting = false;
                    },
                    function(response) {
                        $mdToast.show($mdToast.simple().position('bottom right').textContent("Ocorreu um erro ao enviar seu feedback! Por favor, tente novamente." + response.data.message));
                        $scope.submitting = false;
                    }
                );
        };

        $scope.submitAllFeedback = function() {
            $scope.submitting = true;
            $scope.activeIssue.project = $scope.activeProject;
            $scope.activeIssue.feedback.cochanges = $scope.cochanges;
            $log.debug("Sending feedback of " + $scope.activeIssue.key + "...");
            $http.post("/saveAllFeedback", $scope.activeIssue)
                .then(function(response) {
                        $mdToast.show($mdToast.simple().position('bottom right').textContent(response.data.message));
                        $scope.submitting = false;
                    },
                    function(response) {
                        $mdToast.show($mdToast.simple().position('bottom right').textContent("Ocorreu um erro ao enviar seu feedback! Por favor, tente novamente." + response.data.message));
                        $scope.submitting = false;
                    }
                );
        };

        $scope.showAlert = function(ev) {
            $scope.issueDescription = $sce.trustAsHtml($scope.activeIssue.description);
            $mdDialog.show(
                $mdDialog.alert()
                .parent(angular.element(document.querySelector('#popupContainer')))
                .clickOutsideToClose(true)
                .title($scope.activeIssue.summary)
                .htmlContent($scope.issueDescription)
                .ok('Fechar')
                .targetEvent(ev)
            );
        };
    }
]);
