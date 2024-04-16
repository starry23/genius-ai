const fs = require('fs');
const path = require('path');

const mkdir = dir => {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir);
    }
};

function copyDirectory(src, dest) {
    // 创建目标目录
    mkdir(dest);

    // 读取源目录
    const files = fs.readdirSync(src);

    for (const file of files) {
        const srcPath = path.join(src, file);
        const destPath = path.join(dest, file);

        if (fs.statSync(srcPath).isDirectory()) {
            // 如果是目录，递归复制
            copyDirectory(srcPath, destPath);
        } else {
            // 如果是文件，直接复制
            fs.copyFileSync(srcPath, destPath);
        }
    }
}

// 设置源目录和目标目录
const srcDir = path.join(__dirname, '../base_web');
const destDir = path.join(__dirname,'../../ai-server/src/main/resources/static');

// 执行复制操作
copyDirectory(srcDir, destDir);
