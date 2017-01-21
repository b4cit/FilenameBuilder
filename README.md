# FilenameBuilder
A small Java utility for regenerating new filename from old one. Most methods in this utility supports chain-invocation that allows you to build a whole new name in lines of code.

# Usage Example - Simple append before extension
```Java
	File f_in = new File("C:/myfile.txt");
	// Get old filename, split with '.' into two parts, then append '_new' to first element
	FilenameBuilder fb = new FilenameBuilder(f_in.getName()).appendFirst("_new"); // myfile_new.txt
	File f_out = new File(fb.toString());
```

# Usage Example - Simple change extension
```Java
	File f_in = new File("C:/unknown.bin");
	// Get old filename, split with '.' into two parts, then changes last part(extension)
	FilenameBuilder fb = new FilenameBuilder(f_in.getName()).changeExtension("zip"); // unknown.zip
	File f_out = new File(fb.toString());
```

# Usage Example - Mixed
```Java
	File f_in = new File("C:/secret.txt");
	// Get old filename, split with '.' into two parts
	FilenameBuilder fb = new FilenameBuilder(f_in.getName());
	// secret_encrypted.by b4cit.bin
	fb.appendFirst("_encrypted").add("by b4cit", 1).changeExtension("bin");
	File f_out = new File(fb.toString());
```

# Usage Example - Mixed with negative index
```Java
	FilenameBuilder fb = new FilenameBuilder("com.myapp.todolist.apk");
	// org.myapp.todolist.trial.ver2.zip
	fb.replaceFirst("org").add("ver2", 3).add("trial", -2).changeExtension("zip");
	File f_out = new File(fb.toString());
```

# Known Bugs and ideas
Haven't fully implement the power of backed LinkedList, which may cause bugs on removing elements.
Ability to parse/serialize number like r00,r01,r02...
