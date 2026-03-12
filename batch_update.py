#!/usr/bin/env python3
"""
Batch update templates to use modern-ui CSS and improve button styling
"""
import os
import re
from pathlib import Path

TEMPLATES_DIR = Path(r'c:\Users\DELL\Documents\My projects\Recruiting\recruiting-system\src\main\resources\templates')

def update_css_link(content):
    """Ensure modern-ui.css is linked"""
    if 'modern-ui.css' in content:
        return content
    
    # Find the last CSS link and add modern-ui.css after it
    pattern = r'(<link[^>]*?style\.css[^>]*/>\s*\n)'
    if re.search(pattern, content):
        return re.sub(pattern, r'\1    <link rel="stylesheet" th:href="@{/css/modern-ui.css}" />\n', content, count=1)
    
    # Alternative: add before title
    pattern = r'(    <title)'
    if re.search(pattern, content):
        return re.sub(pattern, r'    <link rel="stylesheet" th:href="@{/css/modern-ui.css}" />\n\1', content, count=1)
    
    return content

def update_button_classes(content):
    """Update button classes to use modern styles"""
    replacements = [
        # Replace simple .btn with .btn-primary
        (r'class="btn"(?![^"]*btn-)', 'class="btn-primary"'),
        # Replace .btn-danger with proper styling
        (r'class="([^"]*?)btn\s+btn-secondary([^"]*?)"', r'class="\1btn-secondary\2"'),
    ]
    
    for old, new in replacements:
        content = re.sub(old, new, content)
    
    return content

def update_badge_classes(content):
    """Update badge classes"""
    # Update badge styling
    content = re.sub(r'class="([^"]*?)status-badge([^"]*?)"', r'class="\1badge badge-primary\2"', content)
    return content

def process_file(file_path):
    """Process a single template file"""
    if not file_path.suffix == '.html':
        return False
    
    # Skip login and register
    if file_path.name in ['login.html', 'register.html']:
        return False
    
    # Skip fragments
    if 'fragment' in file_path.name or file_path.parent.name == 'fragments':
        return False
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original = content
        
        # Apply updates
        content = update_css_link(content)
        content = update_button_classes(content)
        content = update_badge_classes(content)
        
        # Only write if changed
        if content != original:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        return False
    except Exception as e:
        print(f"ERROR processing {file_path.name}: {e}")
        return False

def main():
    if not TEMPLATES_DIR.exists():
        print(f"Templates directory not found: {TEMPLATES_DIR}")
        return
    
    html_files = sorted(TEMPLATES_DIR.glob('*.html'))
    
    updated_count = 0
    skipped_count = 0
    
    for html_file in html_files:
        if html_file.name in ['login.html', 'register.html']:
            print(f"⊘ SKIP {html_file.name} (login/register)")
            skipped_count += 1
        elif 'fragment' in html_file.name:
            print(f"⊘ SKIP {html_file.name} (fragment)")
            skipped_count += 1
        elif process_file(html_file):
            print(f"✓ UPDATE {html_file.name}")
            updated_count += 1
        else:
            print(f"○ {html_file.name} (no changes needed)")
    
    print(f"\n📊 Results: {updated_count} updated, {skipped_count} skipped")

if __name__ == '__main__':
    main()
