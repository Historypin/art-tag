define([
  'jquery',
  'underscore',
  'backbone',
  'templates',
  'models/session'
], function($, _, Backbone, JST, Session){
  'use strict';

  var AppView = Backbone.View.extend({
    template: JST['app/scripts/templates/app.ejs'],

    el: '.container',

    initialize: function () {},

    render: function () {
      var that = this;
      $(this.el).html(this.template);
      // This is the entry point to your app, therefore
      // when the user refreshes the page we should
      // really know if they're authed. We will give it
      // A call back when we know what the auth status is
      Session.getAuth(function () {
        Backbone.history.start();
      })
    }
  });
  return AppView;
});
