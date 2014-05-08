(function() {

    var fs = require('fs');
    var util = require('./CommonUtil.js');

    function readFile(fileName) {
        var data = fs.readFileSync(fileName);
        return JSON.parse(data);
    }

    function InspectedId(id, comment) {
        this.id = id;
        this.comment = comment;
    }

    /**
     * @param {string} text
     * @param {string->true} ids
     */
    function Warning(text, ids) {
        this.text = text;
        this.ids = ids;
    }

    /**
     * @param {array of Warning} warningsToInspect
     * @param {string} knownWarningsFile
     */
    function inspect(warningsToInspect, knownWarningsFile) {
        console.log("\n--------------------- WarningInspector -----------------");
        console.log("Warnings given to inspector: " + warningsToInspect.length);
        var knownWarnings = readFile(knownWarningsFile);
        console.log("Known warnings: " + knownWarnings.length);
        warningsToInspect.forEach(function(warning) {
            // if all ids of the warnings are known, no need to inspect it again
            var knownIds = 0;
            var commentsOfKnown = {};
            Object.keys(warning.ids).forEach(function(id) {
                var isKnown = false;
                knownWarnings.some(function(knownWarning) {
                    if (knownWarning.id === id) {
                        isKnown = true;
                        commentsOfKnown[knownWarning.comment] = true;
                        return true;
                    }
                });
                if (isKnown)
                    knownIds++;
            });
            if (Object.keys(warning.ids).length === knownIds) {
                console.log("Skipping known warning: " + Object.keys(commentsOfKnown));
            } else {
                console.log("===============================\n" + warning.text + "\n");
                var inspectedIdsTemplates = Object.keys(warning.ids).map(function(id) {
                    return new InspectedId(id, "TEMPLATE");
                });
                console.log(JSON.stringify(inspectedIdsTemplates, 0, 2));
                console.log("----------------------------------------");
            }
        });
    }

    exports.inspect = inspect;
    exports.Warning = Warning;

})();