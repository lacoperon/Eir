 var root = 'http://jsonplaceholder.typicode.com';
function httpreq() {
 $.ajax({
   url: root + '/posts/1',
   method: 'GET'
 }).then(function(data) {
   console.log($('textarea').val());
 });
};

setInterval(httpreq, 1000);
