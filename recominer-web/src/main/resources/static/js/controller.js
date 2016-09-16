app.controller("projectController", ['$scope', '$log', '$window', '$http', function ($scope, $log, $window, $http) {
        $scope.projects = null;
        $scope.issues = null;
        $scope.commits = null;
        $scope.files = null;
        $scope.activeProject = null;
        $scope.activeIssue = null;
        $scope.activeCommit = null;
        $scope.activeFile = null;
        $scope.tabIndex = 0;

        $log.debug("Fetching project...");
        $http.get("/projects")
                .then(function (response) {
                    $scope.projects = response.data;
                },
                        function (response) { // optional
                            $scope.projects = null;
                        }
                );

        // Toolbar search toggle
        $scope.toggleSearchProjects = function (element) {
            $scope.showSearchProjects = !$scope.showSearchProjects;
        };
        $scope.toggleSearchIssues = function (element) {
            $scope.showSearchIssues = !$scope.showSearchIssues;
        };
        $scope.toggleSearchCommits = function (element) {
            $scope.showSearchCommits = !$scope.showSearchCommits;
        };
        $scope.toggleSearchFiles = function (element) {
            $scope.showSearchFiles = !$scope.showSearchFiles;
        };

        // Sidenav toggle
        $scope.toggleSidenav = function (menuId) {
            $mdSidenav(menuId).toggle();
        };

        // Load issues
        $scope.getIssuesOf = function (project) {
            $scope.activeProject = project;
            $log.debug("Fetching issues from project " + project.projectName + "...");
            $http.post("/issues", project)
                    .then(function (response) {
                        $scope.issues = response.data;
                    },
                            function (response) { // optional
                                $scope.issues = null;
                            }
                    );
            $scope.tabIndex = 1;
        };

        // Load commits
        $scope.getCommitsOf = function (issue) {
            $scope.activeIssue = issue;
            $log.debug("Fetching commits from issue " + issue.key + "...");
            params = {'project' : $scope.activeProject, 'issue' : issue}
            $http.post("/commits", params)
                    .then(function (response) {
                        $scope.commits = response.data;
                    },
                            function (response) { // optional
                                $scope.commits = null;
                            }
                    );
            $scope.tabIndex = 2;
        };

        // Load files
        $scope.getFilesOf = function (commit) {
            $scope.activeCommit = commit
            $log.debug("Fetching files from commit " + commit.revision + "...");
            $http.post("/files", $scope.activeProject, commit)
                    .then(function (response) {
                        $scope.files = response.data;
                    },
                            function (response) { // optional
                                $scope.files = null;
                            }
                    );
            $scope.tabIndex = 3;
        };

        // Bottomsheet & Modal Dialogs
        $scope.alert = '';
        $scope.showListBottomSheet = function ($event) {
            $scope.alert = '';
            $mdBottomSheet.show({
                template: '<md-bottom-sheet class="md-list md-has-header"><md-list><md-list-item class="md-2-line" ng-repeat="item in items" role="link" md-ink-ripple><md-icon md-svg-icon="{{item.icon}}" aria-label="{{item.name}}"></md-icon><div class="md-list-item-text"><h3>{{item.name}}</h3></div></md-list-item> </md-list></md-bottom-sheet>',
                controller: 'ListBottomSheetCtrl',
                targetEvent: $event
            }).then(function (clickedItem) {
                $scope.alert = clickedItem.name + ' clicked!';
            });
        };

        $scope.showAdd = function (ev) {
            $mdDialog.show({
                controller: DialogController,
                template: '<md-dialog aria-label="Form"> <md-content class="md-padding"> <form name="userForm"> <div layout layout-sm="column"> <md-input-container flex> <label>First Name</label> <input ng-model="user.firstName"> </md-input-container> <md-input-container flex> <label>Last Name</label> <input ng-model="user.lastName"> </md-input-container> </div> <md-input-container flex> <label>Message</label> <textarea ng-model="user.biography" columns="1" md-maxlength="150"></textarea> </md-input-container> </form> </md-content> <div class="md-actions" layout="row"> <span flex></span> <md-button ng-click="answer(\'not useful\')"> Cancel </md-button> <md-button ng-click="answer(\'useful\')" class="md-primary"> Save </md-button> </div></md-dialog>',
                targetEvent: ev,
            })
                    .then(function (answer) {
                        $scope.alert = 'You said the information was "' + answer + '".';
                    }, function () {
                        $scope.alert = 'You cancelled the dialog.';
                    });
        };
    }]);

app.controller('ListBottomSheetCtrl', function ($scope, $mdBottomSheet) {
    $scope.items = [
        {name: 'Share', icon: 'social:ic_share_24px'},
        {name: 'Upload', icon: 'file:ic_cloud_upload_24px'},
        {name: 'Copy', icon: 'content:ic_content_copy_24px'},
        {name: 'Print this page', icon: 'action:ic_print_24px'},
    ];

    $scope.listItemClick = function ($index) {
        var clickedItem = $scope.items[$index];
        $mdBottomSheet.hide(clickedItem);
    };
});

function DialogController($scope, $mdDialog) {
    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    $scope.answer = function (answer) {
        $mdDialog.hide(answer);
    };
}
;

app.controller('DemoCtrl', DemoCtrl);
function DemoCtrl($timeout, $q) {
    var self = this;
    // list of `state` value/display objects
    self.states = loadAll();
    self.selectedItem = null;
    self.searchText = null;
    self.querySearch = querySearch;
    // ******************************
    // Internal methods
    // ******************************
    /**
     * Search for states... use $timeout to simulate
     * remote dataservice call.
     */
    function querySearch(query) {
        var results = query ? self.states.filter(createFilterFor(query)) : [];
        return results;
    }
    /**
     * Build `states` list of key/value pairs
     */
    function loadAll() {
        var allStates = 'Ali Conners, Alex, Scott, Jennifer, \
              Sandra Adams, Brian Holt, \
              Trevor Hansen';
        return allStates.split(/, +/g).map(function (state) {
            return {
                value: state.toLowerCase(),
                display: state
            };
        });
    }
    /**
     * Create filter function for a query string
     */
    function createFilterFor(query) {
        var lowercaseQuery = angular.lowercase(query);
        return function filterFn(state) {
            return (state.value.indexOf(lowercaseQuery) === 0);
        };
    }
}
;
