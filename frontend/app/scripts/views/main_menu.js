/*global define*/

define([
  'jquery',
  'underscore',
  'backbone',
  'templates',
  'models/session'
], function ($, _, Backbone, JST, Session) {
  'use strict';

  var MainMenuView = Backbone.View.extend({
    mainMenuTemplate: JST['app/scripts/templates/main_menu.ejs'],

    loginTemplate: JST['app/scripts/templates/login.ejs'],

    events: {
      'submit form.login' : 'login',
      'click .logout' : 'logout'
    },

    el: '.page',

    initialize: function () {
      var that = this;
      // Bind to the Session auth attribute so we
      // make our view act recordingly when auth changes
      Session.on('change:auth', function () {
        that.render();
      });
    },

    render: function () {
      if(Session.get('auth')){
        this.$el.html(this.mainMenuTemplate({username: Session.get('username')}));
      } else {
        this.$el.html(this.loginTemplate());
      }
    },

    login: function(event) {
      $('[type=submit]', event.currentTarget).val('Logging in').attr('disabled', 'disabled');
      var credentials = $(event.currentTarget).serializeObject();
      Session.login(credentials);
      return false;
    },

    logout: function(event) {
      $(event.currentTarget).text('Logging out').attr('disabled', 'disabled');
      Session.logout();
    }
  });

  return MainMenuView;
});
