const fs = require('fs');
const path = require('path');
const mkdir = dir => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir);
  }
};

function copyDirectory(src, dest) {
  // 创建目标目录
  fs.mkdirSync(dest, { recursive: true });

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
const srcDir = path.join(__dirname, '../dist');
const destDirStatic = path.join(__dirname, '../../ai-server/src/main/resources/static');

// 复制 js, css, img, fonts 文件夹到 static 目录
const copyList = ['/js', '/css', '/img', '/fonts'];
copyList.forEach(_fileName => {
  copyDirectory(srcDir+"/static" + _fileName, destDirStatic+_fileName);
});
const distDir = destDirStatic+"/dist";
mkdir(distDir)
// 复制 favicon.ico 和 index.html 文件到 dist 目录
fs.copyFileSync(srcDir + '/favicon.ico', distDir + '/favicon.ico');
fs.copyFileSync(srcDir + '/index.html', distDir + '/index.html');
