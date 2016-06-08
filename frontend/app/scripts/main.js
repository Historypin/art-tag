/*global require*/
'use strict';

require.config({
  shim: {},
  paths: {
    jquery: '../bower_components/jquery/dist/jquery',
    backbone: '../bower_components/backbone/backbone',
    underscore: '../bower_components/lodash/dist/lodash'
  }
});

require([
  'jquery',
  'router',
  'vm',
  'views/app'
], function ($, Router, Vm, AppView) {

  $.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
      if (o[this.name] !== undefined) {
        if (!o[this.name].push) {
          o[this.name] = [o[this.name]];
        }
        o[this.name].push(this.value || '');
      } else {
        o[this.name] = this.value || '';
      }
    });
    return o;
  };

  // All AJAX requests will be sent to this back-server
  $.ajaxPrefilter(function (options) {
    options.url = 'http://localhost:8080' + options.url;
  });

  var appView = Vm.create({}, 'AppView', AppView);
  Router.initialize({appView: appView});
  appView.render();
});
