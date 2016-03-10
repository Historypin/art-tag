window.User = Backbone.Model.extend({
    urlRoot: "art/user",
    defaults: {
        "id": null,
        "name":  ""
    }
});

//window.UserCollection = Backbone.Collection.extend({
//    model: User,
//    url: "art/user"
//});