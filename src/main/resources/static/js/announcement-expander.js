document.querySelectorAll(".announcement-body").forEach((body) => {
    const toggle = body.nextElementSibling;
    if (!(toggle instanceof HTMLButtonElement)) {
        return;
    }

    body.classList.add("announcement-body--collapsed");
    if (body.scrollHeight <= body.clientHeight) {
        body.classList.remove("announcement-body--collapsed");
        return;
    }

    toggle.hidden = false;
    toggle.addEventListener("click", () => {
        const isExpanded = toggle.getAttribute("aria-expanded") === "true";
        const shouldExpand = !isExpanded;

        body.classList.toggle("announcement-body--collapsed", !shouldExpand);
        toggle.setAttribute("aria-expanded", String(shouldExpand));
        toggle.setAttribute(
            "aria-label",
            shouldExpand ? "Réduire l'annonce" : "Afficher la suite de l'annonce"
        );
        toggle.textContent = shouldExpand ? "Réduire" : "...";
    });
});
