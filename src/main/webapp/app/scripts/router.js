'use strict';

define([
  'jquery',
  'underscore',
  'vm',
  'backbone'
], function ($, _, Vm, Backbone) {

  var AppRouter = Backbone.Router.extend({
    routes: {
      // Default
      '*actions': 'defaultAction'
    }
  });

  var initialize = function (options) {
    var appView = options.appView;
    var appRouter = new AppRouter(options);

    appRouter.on('route:defaultAction', function (actions) {
      require(['views/main_menu'], function (MainMenuView) {
        var mainMenuView = Vm.create(appView, 'MainMenuView', MainMenuView);
        mainMenuView.render();
      });
    });

  };
  return {
    initialize: initialize
  };
});
