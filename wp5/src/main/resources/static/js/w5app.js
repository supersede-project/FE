var app = angular.module('w5app', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider, $controllerProvider, $compileProvider, $filterProvider, $provide) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home'
	}).when('/login', {
		templateUrl : 'login.html',
		controller : 'navigation'
	}).when('/notifications', {
		templateUrl : 'notifications.html',
		controller : 'notifications'
	}).when('/:name*', {
		templateUrl : function(urlattr){
            return urlattr.name + '.html';
        }
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	
	app.controllerProvider = $controllerProvider;
    app.compileProvider    = $compileProvider;
    app.routeProvider      = $routeProvider;
    app.filterProvider     = $filterProvider;
    app.provide            = $provide;

});

app.controller('navigation', function($rootScope, $scope, $http, $location, $route, $interval) {

			$scope.tab = function(route) {
				return $route.current && route === $route.current.controller;
			};

			$rootScope.notificationsCount = 0;
			$rootScope.notifications = [];
			$rootScope.profiles = [];
			$rootScope.applications = [];
			$rootScope.roles = [];
			
			var authenticate = function(credentials, callback) {

				var headers = credentials ? {
					authorization : "Basic "
							+ btoa(credentials.username + ":"
									+ credentials.password)
				} : {};

				headers.TenantId = 'tenant_1';
				
				$http.get('user', {
					headers : headers
				}).success(function(data) {
					if (data.name) {
						$rootScope.authenticated = true;
						$rootScope.roles = data.authorities;
						getProfiles();
					} else {
						cleanAuth();
					}
					callback && callback($rootScope.authenticated);
				}).error(function() {
					cleanAuth();
					callback && callback(false);
				});

			}

			$scope.hasProfile = function(profile) {
				for(var i = 0; i < $rootScope.roles.length; i++) {
				    if ($rootScope.roles[i].authority == profile) {
				        return true;
				    }
				}
				return false;
			}
			
			var getApplications = function(profile)
			{
				$http.get('application', {
					params: { profileId : profile.profileId}
				}).success(function(data) {
					$rootScope.applications[profile.name] = data;
				});
			}
			
			var getProfiles = function()
			{
				$http.get('profile')
				.success(function(data) {
					//clean
					$rootScope.profiles.length = 0;
					angular.copy({}, $rootScope.applications);
					
					for(var i = 0; i < data.length; i++)
					{
						if($scope.hasProfile(data[i].name))
						{
							$rootScope.profiles.push(data[i]);
							getApplications(data[i]);
						}
					}
					
				});
			}
			
			var cleanAuth = function()
			{
				$rootScope.authenticated = false;
				$rootScope.profiles = [];
				$rootScope.applications = [];
			}
			
			authenticate();

			$scope.credentials = {};
			$scope.login = function() {
				authenticate($scope.credentials, function(authenticated) {
					if (authenticated) {
						console.log("Login succeeded")
						$location.path("/");
						$scope.error = false;
						$rootScope.authenticated = true;
					} else {
						console.log("Login failed")
						$location.path("/login");
						$scope.error = true;
						cleanAuth();
					}
				})
			};

			$scope.logout = function() {
				$http.post('logout', {}).success(function() {
					cleanAuth();
					$location.path("/");
				}).error(function(data) {
					console.log("Logout failed")
					cleanAuth();
					$location.path("/");
				});
			}
			
			$scope.readNotification = function()
			{
				var notificationId = this.notif.notificationId;
				$http.put('notification/' + notificationId + '/read').
					success(function(data) {
						
					});
			}
			
			$scope.deleteNotification = function()
			{
				var notificationId = this.notif.notificationId;
				$http.delete('notification/' + notificationId).
					success(function(data) {
					
					});
			}
			
			var stop;
			
			stop = $interval(function() {
				if($rootScope.authenticated)
				{
					$http.get('notification/count').success(function(data) {
						if($rootScope.notificationsCount != data)
						{
							$rootScope.notificationsCount = data;
							$http.get('notification').success(function(data) {
								//clean
								$rootScope.notifications.length = 0;
								angular.copy({}, $rootScope.notifications);
								
								for(var i = 0; i < data.length; i++)
								{
									$rootScope.notifications.push(data[i]);
								}
							});
						}
					});
				}
	        	}, 1000);
			

});

app.controller('notifications', function($scope, $http) {
	
	$scope.totalPages = 1;
	$scope.notificationsLength = 0;
    $scope.itemsPerPage = 5;
    $scope.currentPage = 0;
	$scope.notifications = [];
	
	$scope.range = function () {
        var ret = [];
        var showPages = Math.min(5, $scope.totalPages);
        
        var start = Math.max(1, $scope.currentPage - Math.floor(showPages / 2));
        var end = Math.min($scope.totalPages, $scope.currentPage + Math.ceil(showPages / 2))
        
        if(start == 1)
        {
        	end = start + showPages - 1;
        }
        if(end == $scope.totalPages)
        {
        	start = end - showPages + 1;
        }
        
        for (var i = start; i < start + showPages; i++) {
            ret.push(i);
        }
        
        return ret;
    };
    
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
    };
    
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.currentPage++;
        }
    };
    
    $scope.setPage = function () {
        $scope.currentPage = this.n -1;
    };
	
	$scope.getNotifications = function()
	{
		$http.get('notification', {params: 
				{
					toRead : false
				}
			}).
			success(function(data) {
				angular.copy({}, $scope.notifications);
				
				for(var i = 0; i < data.length; i++)
				{
					$scope.notifications.push(data[i]);
				}
				$scope.notificationsLength = data.length;
				$scope.totalPages = Math.max(1, Math.ceil($scope.notificationsLength / $scope.itemsPerPage));
		});
	}
	
	$scope.readNotification = function()
	{
		var notificationId = this.notif.notificationId;
		$http.put('notification/' + notificationId + '/read').
			success(function(data) {
				
			});
		
		for(var i = 0; i < $scope.notifications.length; i++)
		{
			if($scope.notifications[i].notificationId == notificationId)
			{
				$scope.notifications[i].read = true;
				break;
			}
		}
	}
	
	$scope.deleteNotification = function()
	{
		var notificationId = this.notif.notificationId;
		$http.delete('notification/' + notificationId).
			success(function(data) {
			
			});
		
		for(var i = 0; i < $scope.notifications.length; i++)
		{
			if($scope.notifications[i].notificationId == notificationId)
			{
				$scope.notifications.splice(i, 1);
				break;
			}
		}
		$scope.notificationsLength--;
		$scope.totalPages = Math.max(1, Math.ceil($scope.notificationsLength / $scope.itemsPerPage));
		if($scope.totalPages == $scope.currentPage && $scope.currentPage != 0)
		{
			$scope.currentPage--;
		}
	}
	
	$scope.getNotifications();
	
});

app.controller('home', function($scope, $http) {
	$http.get('wp5-test-app/resource').success(function(data) {
		$scope.greeting = data;
	});
});
