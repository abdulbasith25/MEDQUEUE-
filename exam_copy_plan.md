# Exam Copy / Transfer — Implementation Plan

## 1. Understanding the Navigation Flows

Starting from `admin/exam_types/index`, clicking an exam type leads to `admin/exam/index/{exam_type_id}` which shows **Levels** (exam_categories). From there, the path splits into **4 flows** depending on whether the exam type has a `course_id` and whether the course is SCERT:

```
admin/exam_types/index
  └─► admin/exam/index/{exam_type_id}          [shows Levels (exam_categories)]
        │
        ├─── [NO course_id] ──────────────────────────────────────────────────────────
        │     └─► admin/exam/level_exams/{exam_type_id}/{exam_category_id}
        │               ├── Tab: "Topic Wise" → list of topics
        │               │     └─► admin/exam/topic_exams/{exam_type_id}/{cat_id}/{topic_id}
        │               │               └── [EXAM LISTING PAGE A]  ★
        │               └── Tab: "Topic Less" → inline exam list
        │                         └── [EXAM LISTING PAGE B]  ★  (embedded in level_exams)
        │
        └─── [WITH course_id, NOT SCERT] ────────────────────────────────────────────
        │     └─► admin/exam/level_subjects/{exam_type_id}/{exam_category_id}
        │               └─► admin/exam/subject_lessons/{exam_type_id}/{cat_id}/{subject_id}
        │                         └─► admin/exam/lesson_exams/{exam_type_id}/{cat_id}/{sub_id}/{lesson_id}
        │                                     └── [EXAM LISTING PAGE C]  ★
        │
        └─── [WITH course_id, IS SCERT] ─────────────────────────────────────────────
              └─► admin/exam/level_subjects/{exam_type_id}/{exam_category_id}
                        └─► admin/exam/subject_topics/{exam_type_id}/{cat_id}/{subject_id}
                                  └─► admin/exam/topic_lessons/{exam_type_id}/{cat_id}/{sub_id}/{topic_id}
                                            └─► admin/exam/lesson_exams/{exam_type_id}/{cat_id}/{sub_id}/{lesson_id}
                                                          └── [EXAM LISTING PAGE D]  ★  (same view as C)
```

**All exam listing pages** — `topic_exams.php`, the Topic-Less tab in `level_exams.php`, and `lesson_exams.php` — render the **same columnar table** with the same columns: `#`, `Title`, `Practice`, `Start Date`, `End Date`, `Questions`, `Action`.

---

## 2. What the Copy Transfers

When copying exam(s) to a destination, the system must:
1. Clone the `exam` row — carrying over all metadata (title, mark, minus_mark, cut_off, description, duration, dates, free, is_practice, icon)
2. **Override** the mapping fields based on the destination context:
   - **Flow A** (topic-wise): `exam_type_id`, `exam_category_id`, `exam_topic_id` | clear `subject_id`, `lesson_id`
   - **Flow B** (topic-less): `exam_type_id`, `exam_category_id` | clear `exam_topic_id`, `subject_id`, `lesson_id`
   - **Flow C/D** (course/lesson): `exam_type_id`, `exam_category_id`, `subject_id`, `lesson_id` | clear `exam_topic_id`
3. Duplicate all rows from `exam_questions` (unset `id`, set new `exam_id`)

---

## 3. UI Changes — Exam Listing Pages

### 3.1 Add Checkboxes Column
Add a checkbox column as the **first** `<th>` in the table header and a `<td>` with `<input type="checkbox" class="exam-checkbox" value="{exam_id}">` in each row.

Affected views:
- `Views/Admin/Exam/topic_exams.php`
- `Views/Admin/Exam/lesson_exams.php`
- The "Topic-Less" tab inside `Views/Admin/Exam/level_exams.php`

### 3.2 Add "Copy Exams" Button
Add a **"Copy Exams"** button in the card header's action area (next to Sort / Add Exam), initially **disabled**. It enables as soon as ≥1 checkbox is checked.

```html
<button id="copyExamsBtn" class="btn btn-md btn-warning rounded-pill" disabled
        onclick="openCopyExamsWizard()">
    <i class="mdi mdi-content-copy"></i> Copy Exams
</button>
```

### 3.3 Checkbox JS behaviour
```js
document.querySelectorAll('.exam-checkbox').forEach(function(cb) {
    cb.addEventListener('change', function() {
        var anyChecked = document.querySelectorAll('.exam-checkbox:checked').length > 0;
        document.getElementById('copyExamsBtn').disabled = !anyChecked;
    });
});
```

---

## 4. The Copy Wizard Modal

When the button is clicked → open an AJAX modal (`show_ajax_modal`) pointing to a new endpoint:

```
GET admin/exam/ajax_copy_exams_wizard?ids=1,2,3
```

The modal renders a **multi-step wizard** (could just be cascading dropdowns in a single form) that mirrors the navigation hierarchy:

### Step 1 — Select Target Exam Type
Dropdown of all exam types.

### Step 2 — Select Target Level (exam_category)
Loaded via AJAX from `admin/exam/get_categories_by_type?exam_type_id=X` (already exists).

### Step 3 — Context-sensitive branching
After the type is selected, determine if it has `course_id`:

- **NO course_id** → show:
  - "Destination: Topic-less" radio / or "Pick a Topic" dropdown  
  - If topic: load topics for the category  
- **WITH course_id, NOT SCERT** → show:
  - Subject dropdown → Chapter (lesson) dropdown  
- **WITH course_id, IS SCERT** → show:
  - Subject dropdown → SCERT Topic dropdown → Chapter dropdown  

### Path display
Show the selected breadcrumb path: `Exam Type > Level > Subject > Chapter` so the admin can see the exact destination before confirming.

### Submit
`POST admin/exam/copy_exams` with:
```
exam_ids        = "1,2,3"
target_type_id  = X
target_category_id = Y
destination_type = "topic|topicless|lesson"
target_topic_id  = Z   (if topic)
target_subject_id = S  (if lesson/scert)
target_lesson_id  = L  (if lesson/scert)
```

---

## 5. Backend — New Controller Methods

All in `App\Controllers\Admin\Exam`:

### 5.1 `ajax_copy_exams_wizard()`
- GET: reads `ids` from query string
- Loads all exam types
- Returns view `Admin/Exam/ajax_copy_exams_wizard`

### 5.2 `get_exam_type_info()` (AJAX utility)
- POST: `exam_type_id`
- Returns JSON: `{has_course, is_scert, course_id}`
- Used by wizard to know which dropdowns to show next

### 5.3 `get_subjects_by_type_category()` (AJAX utility)
- GET: `exam_type_id`, `exam_category_id`
- Returns JSON: list of subjects
- (loads subjects for a type+category combo)

### 5.4 `get_lessons_by_subject()` (AJAX utility, already partially exists as `get_lesson_question`)
- POST: `subject_id`
- Returns JSON: list of lessons

### 5.5 `get_scert_topics_by_subject()` (already exists, returns HTML — may reuse)

### 5.6 `copy_exams()` — the core action
```
POST admin/exam/copy_exams
```
Logic:
1. Validate inputs
2. Determine `destination_type` → build the mapping array:
   - `topicless` → `{exam_type_id, exam_category_id, exam_topic_id=0, subject_id=0, lesson_id=0}`
   - `topic`     → `{exam_type_id, exam_category_id, exam_topic_id, subject_id=0, lesson_id=0}`
   - `lesson`    → `{exam_type_id, exam_category_id, exam_topic_id=0, subject_id, lesson_id}`
3. For each exam_id:
   - Fetch exam row
   - Unset `id`
   - Merge mapping array
   - Set audit fields (`created_by`, `created_at`, clear `updated_*`)
   - Insert → get `new_exam_id`
   - Fetch all `exam_questions` for original exam
   - For each question: unset `id`, set `exam_id = new_exam_id`, insert
4. Flash success/error
5. Redirect back to the current page (use `redirect()->back()`)

---

## 6. New View File

`Views/Admin/Exam/ajax_copy_exams_wizard.php`
- Hidden input: `exam_ids`
- Exam Type dropdown  
- Conditional cascading dropdowns (category → subject/topic → lesson)  
- Path breadcrumb display  
- Submit button  
- JS for AJAX chaining + type-info call

---

## 7. Routes to Add

In `app/Config/Routes.php` (or wherever admin routes are):
```php
$routes->get('admin/exam/ajax_copy_exams_wizard', 'Admin\Exam::ajax_copy_exams_wizard');
$routes->post('admin/exam/get_exam_type_info', 'Admin\Exam::get_exam_type_info');
$routes->get('admin/exam/get_subjects_by_type_category', 'Admin\Exam::get_subjects_by_type_category');
$routes->post('admin/exam/copy_exams', 'Admin\Exam::copy_exams');
```

---

## 8. Summary of Files Changed / Created

| File | Action |
|---|---|
| `Views/Admin/Exam/topic_exams.php` | Add checkbox column + Copy button + JS |
| `Views/Admin/Exam/lesson_exams.php` | Add checkbox column + Copy button + JS |
| `Views/Admin/Exam/level_exams.php` | Add checkbox to Topic-Less tab + Copy button + JS |
| `Views/Admin/Exam/ajax_copy_exams_wizard.php` | **NEW** — wizard modal view |
| `Controllers/Admin/Exam.php` | Add: `ajax_copy_exams_wizard()`, `get_exam_type_info()`, `get_subjects_by_type_category()`, `copy_exams()` |
| `Config/Routes.php` | Add 4 new routes |

> [!IMPORTANT]
> The existing `transfer_exam` / `bulk_transfer_exam` endpoints only handle the **no-course_id** path. The new `copy_exams` handler will be **universal** — covering all 3 destination types (topicless, topic, lesson) so it works across all flows.

> [!NOTE]
> The existing `ajax_transfer.php` and `ajax_bulk_transfer.php` views (and their controller methods) will remain untouched — they are used for the existing per-question bulk-transfer flow. This is a new parallel feature specifically for copying entire **exams**.
