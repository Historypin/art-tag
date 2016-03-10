window.UserView = Backbone.View.extend({

    tagName: "div", // Not required since 'div' is the default if no el or tagName specified

    initialize: function() {
        this.template = _.template(tpl.get('user-details'));
        this.model.bind("change", this.render, this);
    },

    render: function(eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    },

    events: {
        "change input": "change",
        "click .save": "saveUser",
        "click .delete": "deleteUser"
    },

    change: function(event) {
        var target = event.target;
        console.log('changing je to super ' + target.id + ' from: ' + target.defaultValue + ' to: ' + target.value);
        // You could change your model on the spot, like this:
        // var change = {};
        // change[target.name] = target.value;
        // this.model.set(change);
    },

    saveUser: function() {
        this.model.set({
            name: $('#name').val()
        });
        if (this.model.isNew()) {
            var self = this;
            app.wineList.create(this.model, {
                success: function() {
                    app.navigate('user/'+self.model.id, false);
                }
            });
        } else {
            this.model.save();
        }

        return false;
    },

    deleteUser: function() {
        this.model.destroy({
            success: function() {
                alert('User deleted successfully bla');
                window.history.back();
            }
        });
        return false;
    }
});