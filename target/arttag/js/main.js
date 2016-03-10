Backbone.View.prototype.close = function () {
    console.log('Closing view ' + this);
    if (this.beforeClose) {
        this.beforeClose();
    }
    this.remove();
    this.unbind();
};

var AppRouter = Backbone.Router.extend({

    routes: {
        ""			: "list",
        "user/new"	: "newUser",
        "user/:id"	: "userDetails"
    },
    initialize:function () {
        $('#header').html(new HeaderView().render().el);
    },

    list:function () {
        this.userList = new UserCollection();
        this.userListView = new UserListView({model:this.userList});
        this.userList.fetch();
        $('#sidebar').html(this.userListView.render().el);
    },

    userDetails:function (id) {
        this.user = this.userList.get(id);
        if (app.userView) app.userView.close();
        this.userView = new UserView({model:this.user});
        $('#content').html(this.userView.render().el);
    }

});


tpl.loadTemplates(['header', 'user-details', 'user-list-item'], function() {
    app = new AppRouter();
    Backbone.history.start();
});
