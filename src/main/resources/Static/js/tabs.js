
// Minimal vanilla-JS tab switcher — replaces JavaFX's TabPane on the
// Nurse and Admin dashboards. No build step, no dependencies.
document.addEventListener("DOMContentLoaded", function () {
    var tabButtons = document.querySelectorAll("[data-tab-target]");
    if (!tabButtons.length) {
        return;
    }

    function activate(name) {
        document.querySelectorAll("[data-tab-target]").forEach(function (btn) {
            btn.classList.toggle("active", btn.getAttribute("data-tab-target") === name);
        });
        document.querySelectorAll("[data-tab-panel]").forEach(function (panel) {
            panel.classList.toggle("active", panel.getAttribute("data-tab-panel") === name);
        });
    }

    tabButtons.forEach(function (btn) {
        btn.addEventListener("click", function () {
            activate(btn.getAttribute("data-tab-target"));
        });
    });

    var requested = new URLSearchParams(window.location.search).get("tab");
    var firstTab = tabButtons[0].getAttribute("data-tab-target");
    activate(requested || firstTab);
});