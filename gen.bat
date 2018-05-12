@echo off
set now=%cd%
::首先执行vs配置编译器(cl.exe)所需环境的脚本
call "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\VC\Auxiliary\Build\vcvarsall.bat" x86
::打开注释，以便查看结果
::echo on 
::重要!因为vs配置环境变量的脚本会自动切到c:\Users\..\source
::因此我们需要切回到我们的当前项目环境，实际上直接e:就可以回到这个具体路径了)
e: 

::遍历gen.txt中的每一行（每一个学生的项目路径），来生成exe
for /f "delims=" %%a in (gen.txt) do (
    :: 跑到该同学的目录下
    cd %dp~0%%%a
    ::指定生成的exe文件名为wc.exe,在该同学的当前目录下
    cl *.cpp /EHsc /Fewc.exe
    ::回到项目的目录
    cd %now%
    endlocal
)
