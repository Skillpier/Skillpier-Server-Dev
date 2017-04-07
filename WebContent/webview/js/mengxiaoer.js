var mengxiaoer = angular.module("mengxiaoer",[
		"ngRoute",
		"ngAnimate",
		"ngTouch",
		"ngSanitize",
		"wrapOwlcarousel",
		'infinite-scroll'
	]).config([
		"$routeProvider",
		"$httpProvider",
		function($routeProvider,$httpProvider){

		}
	]).run(function($rootScope){
		
	});

mengxiaoer.controller('endlessController', function($scope, Reddit) {
  $scope.reddit = new Reddit();
});

// Reddit constructor function to encapsulate HTTP and pagination logic
mengxiaoer.factory('Reddit', function($http) {
  var Reddit = function() {
    this.items = [];
    this.busy = false;
    this.after = '1';
  };

  Reddit.prototype.nextPage = function(type) {
    if (this.busy) return;
    if (this.after == -1) return;
    this.busy = true;
    var url = "/app/Gongyi/wangqiPagination?after=" + this.after+"&type="+type;
    $http.get(url).success(function(data) {
    	console.log(data)
      var items = data.wangqi;
      console.log(items)
      for (var i = 0; i < items.length; i++) {
        this.items.push(items[i]);
      }
      this.after = this.items[this.items.length - 1].id;
      this.busy = false;
    }.bind(this));
  };

  return Reddit;
});