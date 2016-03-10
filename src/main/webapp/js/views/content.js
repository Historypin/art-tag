window.UserView = Backbone.View.extend({

    tagName: "div", // Not required since 'div' is the default if no el or tagName specified

    initialize: function() {
        this.template = _.template(tpl.get('content'));
        this.model.bind("change", this.render, this);
    },

    render: function(eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    },

    events: {
        "change input": "change",
        "click .logIn": "logIn",
        "click .delete": "deleteUser"
    },

    change: function(event) {
        var target = event.target;
        console.log('changing ' + target.id + ' from: ' + target.defaultValue + ' to: ' + target.value);
        // You could change your model on the spot, like this:
        var change = {};
        change[target.name] = target.value;
        this.model.set(change);
        this.model.save();
    },

    logIn: function() {
        this.model.set({
            name: $('#name').val()
        });
//        app.navigate("user/menu", true);
//        window.location.reload();
        return false;
    },

    deleteUser: function() {
        this.model.destroy({
            success: function() {
                alert('User deleted successfully');
                window.history.back();
            }
        });
        return false;
    }
});