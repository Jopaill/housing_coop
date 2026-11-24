const toggleButton = document.getElementById("nav-toggle");
const nav = document.getElementById("main-nav");

toggleButton.addEventListener("click", () => {
    const isOpen = !nav.classList.contains("hidden");

    nav.classList.toggle("hidden");
    toggleButton.classList.toggle("left-48", !isOpen);
    toggleButton.setAttribute("aria-expanded", String(!isOpen));
});
