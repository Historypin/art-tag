Backbone.View.prototype.close = function () {
    console.log('Closing view ' + this);
    if (this.beforeClose) {
        this.beforeClose();
    }
    this.remove();
    this.unbind();
};

var AppRouter = Backbone.Router.extend({

    initialize: function() {
        $('#header').html( new HeaderView().render().el );
    },

    routes: {
        ""			: "newUser",
        "user/menu"	: "menu",
        "user/:id"	: "content"
    },

    list: function() {
        this.before();
    },

    content: function(id) {
        this.before(function() {
            var user = app.user.get(id);
            console.log("id "+ id)
            app.showView( '#content', new UserView({model: user}) );
        });
    },

    newUser: function() {
        this.before(function() {
            app.showView( '#content', new UserView({model: new User()}) );
        });
    },

    menu: function(id) {
        this.before(function() {
            var user = app.user.get(id);
            console.log(id)
//            app.showView( '#menu', new MenuView({model: user}) );
            $('#menu').html( new MenuView({model: user}).render().el );
        });

    },

    showView: function(selector, view) {
        if (this.currentView)
            this.currentView.close();
        $(selector).html(view.render().el);
        this.currentView = view;
        return view;
    },

    before: function(callback) {
        if (this.user) {
            if (callback) callback();
        } else {
            this.user = new User();
            this.user.fetch({success: function() {

                if (callback) callback();
            }});
        }
    }

});


tpl.loadTemplates(['header', 'content', 'menu'], function() {
    app = new AppRouter();
    Backbone.history.start();
});
