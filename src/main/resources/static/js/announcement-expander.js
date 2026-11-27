const announcements = Array.from(document.querySelectorAll(".announcement-row"));

announcements.forEach((announcement) => {
    const body = announcement.querySelector(".announcement-body");
    if (body instanceof HTMLElement) {
        body.classList.add("announcement-body--collapsed");
        if (body.scrollHeight <= body.clientHeight) {
            body.classList.remove("announcement-body--collapsed");
            body.dataset.expandable = "false";
        } else {
            body.dataset.expandable = "true";
        }
    }
});

function toggleAnnouncementSelection(selectedAnnouncement) {
    const isSelected = selectedAnnouncement.dataset.selected === "true";

    if (isSelected) {
        announcements.forEach((announcement) => {
            announcement.hidden = false;
            announcement.dataset.selected = "false";
            announcement.setAttribute("aria-pressed", "false");
            announcement.querySelector(".announcement-modification-button")?.setAttribute("hidden", "");
            const body = announcement.querySelector(".announcement-body");
            if (body?.dataset.expandable === "true") {
                body.classList.add("announcement-body--collapsed");
            }
        });
        return;
    }

    announcements.forEach((announcement) => {
        const shouldSelect = announcement === selectedAnnouncement;
        const body = announcement.querySelector(".announcement-body");
        announcement.hidden = !shouldSelect;
        announcement.dataset.selected = String(shouldSelect);
        announcement.setAttribute("aria-pressed", String(shouldSelect));
        const modificationButton = announcement.querySelector(".announcement-modification-button");
        if (shouldSelect) {
            modificationButton?.removeAttribute("hidden");
        } else {
            modificationButton?.setAttribute("hidden", "");
        }
        if (body?.dataset.expandable === "true") {
            body.classList.toggle("announcement-body--collapsed", !shouldSelect);
        }
    });

    selectedAnnouncement.querySelector(".announcement-body")?.classList.remove("announcement-body--collapsed");
}

announcements.forEach((announcement) => {
    announcement.addEventListener("click", (event) => {
        if (event.target.closest(".announcement-modification-button")) {
            return;
        }
        toggleAnnouncementSelection(announcement);
    });

    announcement.addEventListener("keydown", (event) => {
        if (event.target !== announcement) {
            return;
        }
        if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            toggleAnnouncementSelection(announcement);
        }
    });
});

const editDialog = document.getElementById("announcement-edit-dialog");
const editForm = document.getElementById("announcement-edit-form");
const editTitle = document.getElementById("announcement-edit-title");
const editBody = document.getElementById("announcement-edit-body");
const editStatus = document.getElementById("announcement-edit-status");
const editClose = document.getElementById("announcement-edit-close");
const deleteButton = document.getElementById("announcement-delete-button");
const actionStatus = document.getElementById("announcement-action-status");

let editAnnouncementId = null;

if (editDialog instanceof HTMLDialogElement) {
    editClose.addEventListener("click", () => editDialog.close());

    editForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        editStatus.hidden = true;

        const requestBody = new URLSearchParams({
            id: String(editAnnouncementId),
            title: editTitle.value,
            bodyMarkdown: editBody.value
        });

        try {
            const response = await fetch("/modifyAnnouncementById", {
                method: "PUT",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: requestBody
            });

            if (!response.ok) {
                editStatus.textContent = response.status === 403
                    ? "Vous ne pouvez pas modifier cette annonce."
                    : "Impossible de modifier cette annonce.";
                editStatus.hidden = false;
                return;
            }

            window.location.reload();
        } catch {
            editStatus.textContent = "Impossible de modifier cette annonce.";
            editStatus.hidden = false;
        }
    });

    deleteButton.addEventListener("click", async () => {
        if (!window.confirm("Supprimer définitivement cette annonce ?")) {
            return;
        }

        editStatus.hidden = true;
        try {
            const response = await fetch(`/deleteAnnouncementById?id=${encodeURIComponent(editAnnouncementId)}`, {
                method: "DELETE"
            });

            if (!response.ok) {
                editStatus.textContent = response.status === 403
                    ? "Vous ne pouvez pas supprimer cette annonce."
                    : "Impossible de supprimer cette annonce.";
                editStatus.hidden = false;
                return;
            }

            window.location.reload();
        } catch {
            editStatus.textContent = "Impossible de supprimer cette annonce.";
            editStatus.hidden = false;
        }
    });
}

document.querySelectorAll(".announcement-modification-button").forEach((button) => {
    button.addEventListener("click", async (event) => {
        event.stopPropagation();
        const announcement = button.closest(".announcement-row");
        if (!(announcement instanceof HTMLElement)) {
            return;
        }

        actionStatus.hidden = true;
        const id = announcement.dataset.announcementId;

        try {
            const response = await fetch(`/canModifyAnnouncement?id=${encodeURIComponent(id)}`);
            if (!response.ok) {
                actionStatus.textContent = "Impossible de vérifier les permissions de modification.";
                actionStatus.hidden = false;
                return;
            }

            const canModify = await response.json();
            if (!canModify) {
                actionStatus.textContent = "Vous ne pouvez pas modifier cette annonce.";
                actionStatus.hidden = false;
                return;
            }

            editAnnouncementId = id;
            editTitle.value = announcement.dataset.announcementTitle || "";
            editBody.value = announcement.dataset.announcementBody || "";
            editStatus.hidden = true;
            editDialog.showModal();
        } catch {
            actionStatus.textContent = "Impossible de vérifier les permissions de modification.";
            actionStatus.hidden = false;
        }
    });
});
