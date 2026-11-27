const announcementDialog = document.getElementById("announcement-dialog");
const openAnnouncementDialogButton = document.getElementById("open-announcement-dialog");
const closeAnnouncementDialogButton = document.getElementById("close-announcement-dialog");
const announcementBody = document.getElementById("announcement-body-markdown");
const announcementBodyCount = document.getElementById("announcement-body-count");
const markdownEditor = document.getElementById("announcement-markdown-editor");
const markdownPreview = document.getElementById("announcement-markdown-preview");
const markdownPreviewToggle = document.getElementById("announcement-preview-toggle");
const markdownPreviewError = document.getElementById("announcement-preview-error");
const markdownEditorError = document.getElementById("announcement-editor-error");

if (announcementDialog instanceof HTMLDialogElement) {
    openAnnouncementDialogButton.addEventListener("click", () => {
        announcementDialog.showModal();
    });

    closeAnnouncementDialogButton.addEventListener("click", () => {
        announcementDialog.close();
    });

    if (announcementDialog.dataset.open === "true") {
        announcementDialog.showModal();
    }

    const updateBodyCount = () => {
        const characterCount = announcementBody.value.length;
        announcementBodyCount.hidden = characterCount <= 3000;
        announcementBodyCount.textContent = `${characterCount} / 4 000 caractères`;
    };

    const resizeAnnouncementBody = () => {
        announcementBody.style.height = "auto";
        announcementBody.style.height = `${announcementBody.scrollHeight}px`;
    };

    announcementBody.addEventListener("input", () => {
        markdownEditorError.hidden = true;
        updateBodyCount();
        resizeAnnouncementBody();
    });
    updateBodyCount();
    resizeAnnouncementBody();

    const applyMarkdownEdit = (replacement, start, end, selectionStart, selectionEnd) => {
        const nextValue = announcementBody.value.slice(0, start) + replacement + announcementBody.value.slice(end);
        if (nextValue.length > announcementBody.maxLength) {
            markdownEditorError.textContent = "Cette mise en forme dépasserait la limite de 4 000 caractères.";
            markdownEditorError.hidden = false;
            announcementBody.focus();
            return;
        }

        announcementBody.setRangeText(replacement, start, end, "select");
        announcementBody.setSelectionRange(selectionStart, selectionEnd);
        announcementBody.focus();
        announcementBody.dispatchEvent(new Event("input", { bubbles: true }));
    };

    const insertWrappedText = (prefix, suffix, placeholder) => {
        const start = announcementBody.selectionStart;
        const end = announcementBody.selectionEnd;
        const selectedText = announcementBody.value.slice(start, end);
        const textToWrap = selectedText || placeholder;
        const replacement = `${prefix}${textToWrap}${suffix}`;

        applyMarkdownEdit(
            replacement,
            start,
            end,
            start + prefix.length,
            start + prefix.length + textToWrap.length
        );
    };

    const prefixSelectedLines = (prefixForLine) => {
        const value = announcementBody.value;
        const start = announcementBody.selectionStart;
        const end = announcementBody.selectionEnd;
        const firstLineStart = value.lastIndexOf("\n", Math.max(0, start - 1)) + 1;
        const nextLineBreak = value.indexOf("\n", end);
        const lastLineEnd = nextLineBreak === -1 ? value.length : nextLineBreak;
        const lines = value.slice(firstLineStart, lastLineEnd).split("\n");
        const prefixedLines = lines.map((line, index) => `${prefixForLine(index)}${line}`).join("\n");

        applyMarkdownEdit(
            prefixedLines,
            firstLineStart,
            lastLineEnd,
            firstLineStart,
            firstLineStart + prefixedLines.length
        );
    };

    document.querySelectorAll("[data-markdown-action]").forEach((button) => {
        button.addEventListener("click", () => {
            switch (button.dataset.markdownAction) {
                case "bold":
                    insertWrappedText("**", "**", "texte");
                    break;
                case "italic":
                    insertWrappedText("*", "*", "texte");
                    break;
                case "bullets":
                    prefixSelectedLines(() => "- ");
                    break;
                case "numbered":
                    prefixSelectedLines((index) => `${index + 1}. `);
                    break;
            }
        });
    });

    markdownPreviewToggle.addEventListener("click", async () => {
        if (!markdownPreview.hidden) {
            markdownPreview.hidden = true;
            markdownEditor.hidden = false;
            markdownPreviewToggle.textContent = "Aperçu";
            markdownPreviewToggle.setAttribute("aria-pressed", "false");
            markdownPreviewError.hidden = true;
            announcementBody.focus();
            return;
        }

        markdownPreviewError.hidden = true;
        markdownPreviewToggle.disabled = true;
        const requestBody = new URLSearchParams({ bodyMarkdown: announcementBody.value });

        try {
            const response = await fetch("/home/announcements/preview", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: requestBody
            });

            if (response.redirected) {
                window.location.assign(response.url);
                return;
            }

            const responseText = await response.text();
            if (!response.ok) {
                markdownPreviewError.textContent = responseText || "Impossible de générer l'aperçu.";
                markdownPreviewError.hidden = false;
                return;
            }

            markdownPreview.innerHTML = responseText;
            markdownEditor.hidden = true;
            markdownPreview.hidden = false;
            markdownPreviewToggle.textContent = "Modifier le Markdown";
            markdownPreviewToggle.setAttribute("aria-pressed", "true");
        } catch {
            markdownPreviewError.textContent = "Impossible de générer l'aperçu. Vérifiez votre connexion et réessayez.";
            markdownPreviewError.hidden = false;
        } finally {
            markdownPreviewToggle.disabled = false;
        }
    });
}
