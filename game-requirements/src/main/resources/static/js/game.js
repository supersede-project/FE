var app = angular.module('w5app');

app.controllerProvider.register('home', function($scope, $http, $rootScope) {
	
	$scope.loggedUser = $rootScope.user;
	$scope.user = null;
	
	$http.get('game-requirements/user/' + $scope.loggedUser.userId)
	.success(function(data) {
		$scope.user = data;
	});
	
});

app.controllerProvider.register('leaderboard', function($scope, $http) {
	
    $scope.users = [];
    $scope.usersCount = 0;
       
    $http.get('game-requirements/user')
	.success(function(data) {
		$scope.users.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.users.push(data[i]);
		}
		 $scope.usersCount = data.length;
	});
});


app.controllerProvider.register('criterias_leaderboard', function($scope, $http) {
	
	$scope.valutationCriterias = [];
    $scope.criteriaUsers = []; 
    $scope.selectedCriteria = null;
    
    $http.get('game-requirements/criteria')
	.success(function(data) {
		$scope.valutationCriterias.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.valutationCriterias.push(data[i]);
		}
	});
    	
    $scope.selectedCriteriaChanged = function(){   	
    	$http.get('game-requirements/user/criteria/' + $scope.selectedCriteria.criteriaId)
		.success(function(data) {
			$scope.criteriaUsers.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.criteriaUsers.push(data[i]);
			}
		});
    }    
});

app.controllerProvider.register('move_creation', function($scope, $http) {
	
	$scope.valutationCriterias = [];
    $scope.users = []; 
    $scope.requirements = []; 
    $scope.selectedCriteria = null;
    $scope.selectedPlayerOne = null;
    $scope.selectedPlayerTwo = null;
    $scope.selectedFirstRequirement = null;
    $scope.selectedSecondRequirement = null;
    $scope.moveName = '';
    $scope.moveTimer = 3600;
    
    $http.get('game-requirements/criteria')
	.success(function(data) {
		$scope.valutationCriterias.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.valutationCriterias.push(data[i]);
		}
	});
    	
	$http.get('game-requirements/user')
	.success(function(data) {
		$scope.users.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.users.push(data[i]);
		}
	});
	
    $http.get('game-requirements/requirement')
	.success(function(data) {
		$scope.requirements.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirements.push(data[i]);
		}
	});
    
    $scope.createMove = function(){
    	$http({
			url: "game-requirements/move",
	        data: {
	        	name : $scope.moveName,
	        	timer : $scope.moveTimer,
	        	firstRequirement : $scope.selectedFirstRequirement,
        		secondRequirement : $scope.selectedSecondRequirement,
        		firstPlayer : $scope.selectedPlayerOne,
        		secondPlayer : $scope.selectedPlayerTwo,
        		criteria : $scope.selectedCriteria
	        },
	        method: 'POST'
	    }).success(function(data){
	    	
	    }).error(function(err){
	    	console.log(err);
	    });
    };
    
});

app.controllerProvider.register('user_moves', function($scope, $http) {
	
    $scope.moves = [];
    $scope.movesCount = 0;
    
    $http.get('game-requirements/move')
	.success(function(data) {
		$scope.moves.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.moves.push(data[i]);
		}
		 $scope.movesCount = data.length;
	});
                 
});

app.controllerProvider.register('play_view', function($scope, $http, $location) {
	
	$scope.moveId = $location.search()['moveId'];
	$scope.move = null;
	
	$http.get('game-requirements/move/' + $scope.moveId)
	.success(function(data) {
		$scope.move = data;
	});
	 
    $scope.addRequirement = function(selectedRequirementId){
    	
    	$http.put('game-requirements/move/' + $scope.moveId + '/requirement/' + selectedRequirementId)
    	.success(function(data) {
    		
    	});
    };
	
});

app.controllerProvider.register('judge_view', function($scope, $http, $location) {
	
	$scope.judgeMoveId = $location.search()['judgeMoveId'];
	$scope.judgeMove = null;
	
	$http.get('game-requirements/judgemove/' + $scope.judgeMoveId)
	.success(function(data) {
		$scope.judgeMove = data;
	});
	
	$scope.needArguments = function(){
		$http.put('game-requirements/judgemove/'+ $scope.judgeMoveId + '/needarguments')
		.success(function(data) {
		});
    };
    
});

app.controllerProvider.register('judge_moves', function($scope, $http) {
    
	$scope.judgemoves = [];
	
	 $http.get('game-requirements/judgemove')
		.success(function(data) {
			$scope.judgemoves.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.judgemoves.push(data[i]);
			}
		});
    
});

app.controllerProvider.register('argue_view', function($scope, $http, $location) {
    	
	$scope.moveId = $location.search()['moveId'];
	$scope.argumentChoice = "existing";
	$scope.argumentContent = "";
	$scope.selectedArgument = null;
	$scope.arguments = [];
	$scope.move = null;	
	
	$http.get('game-requirements/move/' + $scope.moveId)
	.success(function(data) {
		$scope.move = data;
	});
	
	$http.get('game-requirements/argument')
	.success(function(data) {
		$scope.arguments.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.arguments.push(data[i]);
		}
	}); 
	 
	$scope.sendArgument = function(){
		 
		if($scope.argumentChoice == "insert"){
	    $http({
			url: "game-requirements/argument",
		       data: {
		        content : $scope.argumentContent,
		       },
		       method: 'POST'
		   }).success(function(data){
		    // SET THE ARGUMENT IN JUDGE_MOVES
		   }).error(function(err){
		    console.log(err);
		   });
		}
		 
		if($scope.argumentChoice == "existing"){
			 
			// SET THE ARGUMENT IN JUDGE_MOVES
		}
		 
	};
	
});

