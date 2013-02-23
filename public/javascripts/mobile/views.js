var Views = (function () {


    function Views() {
    }

    var shoppingListItemTemplate =
        "<input id=\"$ID$\" name=\"$DESCRIPTION$\" type=\"checkbox\" $CHECKED$ "+
                   "data-varetype=\"$VARETYPE$\""   +
                   "data-recipe=\"$RECIPE$\" />"       +
            "<label for=\"$ID$\"><span>$AMOUNT$</span> $UNIT$ $DESCRIPTION$</label>";




    Views.prototype.injectCheckbox = function(page, target, id, amount, unit, description, varetype, recipe, checked) {

         if (varetype == null ) varetype = "";
         if (recipe == null ) recipe = "";

         var injectString = shoppingListItemTemplate.replace("$ID$", id).replace("$ID$", id).replace("$DESCRIPTION$", description).replace("$DESCRIPTION$", description).replace("$VARETYPE$", varetype).replace("$RECIPE$", recipe).replace("$AMOUNT$", amount).replace("$UNIT$", unit);
         if(checked != null)
         {
             injectString = injectString.replace("$CHECKED$","checked=\"true\"");
         }
         else{
             injectString = injectString.replace("$CHECKED$","");
         }

        $(page + " " + target + " .ui-controlgroup-controls").prepend(injectString).parent().trigger("create");
        $(page + " fieldset").controlgroup("refresh");
}
    
    return Views;
  
  })();
  
  var views = new Views();
  
  
    