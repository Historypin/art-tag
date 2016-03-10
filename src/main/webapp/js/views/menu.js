window.MenuView = Backbone.View.extend({

    initialize: function() {
        this.template = _.template(tpl.get('menu'));
        console.log('Meno'+this.model);
    },

    render: function() {
        $(this.el).html(this.template());
        return this;
    }

//    events: {
//        "click .new"    : "newUser"
//    },
//
//    menu: function(event) {
//        app.navigate("user/new", true);
//        return false;
//    }
});
