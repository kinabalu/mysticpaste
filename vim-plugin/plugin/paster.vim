" paster.vim - send text to a web "pastebin".

" Copyright (c) 2009, Eugene Ciurana (pr3d4t0r)
" All rights reserved.
"
" Redistribution and use in source and binary forms, with or without
" modification, are permitted provided that the following conditions are met:
"     * Redistributions of source code must retain the above copyright
"       notice, this list of conditions, the URL http://eugeneciurana.com/paster.vim,
"       and the following disclaimer.
"     * Redistributions in binary form must reproduce the above copyright
"       notice, this list of conditions, the URL http://eugeneciurana.com/paster.vim,
"       and the following disclaimer in the documentation and/or other materials
"       provided with the distribution.
"     * Neither the name Eugene Ciurana, nor pr3d4t0r, nor the
"       names of its contributors may be used to endorse or promote products
"       derived from this software without specific prior written permission.
"
" THIS SOFTWARE IS PROVIDED BY EUGENE CIURANA ''AS IS'' AND ANY
" EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
" WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
" DISCLAIMED. IN NO EVENT SHALL EUGENE CIURANA BE LIABLE FOR ANY
" DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
" (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
" LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
" ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
" (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
" SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
"
"
" CONTRIBUTOR           EMAIL                         IRC /NICK
" ----------            -----                         ---------
" Eugene Ciurana        http://ciurana.eu/contact     pr3d4t0r
" Andrew Lombardi       http://www.mysticcoders.com   kinabalu
" Matt Wozniski         mjw@drexel.edu                godlygeek
"
" Special thanks to stepnem, iamjay_, jerbear, and other denizens of the
" #vim channel (irc://irc.freenode.net/#vim)


" *** Configuration ***

" *************** Begin default configuration ********************

" Default pastebin configuration for http://eugeneciurana.com/pastebin/ and 
" http://ciurana.eu/pastebin/ - these are the ##java channel default pastebin
" sites:

" let s:PASTE_COMMAND         = 'curl'
" let s:PASTE_CONTROL         = '-isv'
" let s:PASTE_FIXED_ARGUMENTS = '-d "parentID=&paste=Send&remember=0&expiry=d"'
" let s:PASTE_FORMAT          = '-d "format=textFormat"'
" let s:PASTE_NICK            = '-d "poster=nickID"'
" let s:PASTE_PAYLOAD         = '-d "@-"'

" PASTE_RESPONSE_FLAG is a regular expression used for identifying
" which line in a list of output lines from the PASTE_COMMAND contains
" the URL of the successful paste operation.  The response varies
" from one server to the next and must be implemented for each
" pastebin.
"
" Given a response like:
"
" 1. Congratulations - your paste succeded!
" 2. Location: http://servername.tld/path/to/pastebin/locator
" 3. Have a nice day
"
" PASTE_RESPONSE_FLAG must contain a regex that detects only the
" second line.

" let s:PASTE_RESPONSE_FLAG   = "^Location"

"                                " vim-filetype   pastebin-format
" let s:PASTE_SYNTAX_OPTIONS  = {   'awk':         'awk',
"                                \ 'sh':          'bash',
"                                \ 'c':           'c',
"                                \ 'cpp':         'cpp',
"                                \ 'html':        'html4strict',
"                                \ 'java':        'java',
"                                \ 'javascript':  'javascript',
"                                \ 'perl':        'perl',
"                                \ 'php':         'php',
"                                \ 'python':      'python',
"                                \ 'ruby':        'ruby',
"                                \
"                                \ 'default':     'text' }
"
" let s:PASTE_TEXT_AREA       = 'code2'
" let s:PASTE_URI             = 'http://eugeneciurana.com/pastebin/'
"
" The ParseLocationFrom() function is system dependent because every
" pastebin returns this information in a sliglhtly different format
" from others.
"
" The function accepts a string containing the URL of the successful
" paste operation, and extracts the URL itself from it.
"
" The function MUST return a string of the form:
" 
" Location: http://servername.tld/path/to/pastebin/locator
"
" The subcomponent that copies the contents to the GUI clipboard
" expects the response string in that format.
"
" function! s:ParseLocationFrom(line)
"   return substitute(a:line, '[[:space:]\r\n]\+$', '', '')
" endfunction

" *************** End default configuration ********************


" *************** Begin AWK Paste! configuration ***************

" let s:PASTE_COMMAND         = 'curl'
" let s:PASTE_CONTROL         = '-isv'
" let s:PASTE_FIXED_ARGUMENTS = '-d "description=Pastebin%20plugin%20for%20vim%20post"'
" let s:PASTE_FORMAT          = '-d "format=textFormat"'
" let s:PASTE_NICK            = '-d "name=nickID"'
" let s:PASTE_PAYLOAD         = '-d "@-"'
" let s:PASTE_RESPONSE_FLAG   = "AWK Paste"
" 
"                                 " vim-filetype   pastebin-format
" let s:PASTE_SYNTAX_OPTIONS  = {   'awk':         'awk',
"                                 \ 'sh':          'bash',
"                                 \ 'c':           'c',
"                                 \ 'cpp':         'cpp',
"                                 \ 'html':        'html4strict',
"                                 \ 'java':        'java',
"                                 \ 'javascript':  'javascript',
"                                 \ 'perl':        'perl',
"                                 \ 'php':         'php',
"                                 \ 'python':      'python',
"                                 \ 'ruby':        'ruby',
"                                 \ 'default':     'text' }
" 
" let s:PASTE_TEXT_AREA       = 'content'
" let s:PASTE_URI             = 'http://awkpaste.blisted.org/cgi/paste.cgi'
" 
" function! s:ParseLocationFrom(line)
"   let tokens  = split(a:line, "\"")
"   let locator = "n/a"
" 
"   if len(tokens) > 0x00
"     for item in tokens
"       if (match(item, "http:") > -1)
"         let locator = item
"       endif
"     endfor
"   endif
" 
"   return "Location: ".locator
" endfunction

" *************** Begin AWK Paste! configuration ***************


" *************** Begin MysticPaste.com configuration **********
"
" Author:  Andrew Lombardi (kinabalu) - http://www.mysticcoders.com
"
let s:PASTE_COMMAND         = 'curl'
let s:PASTE_CONTROL         = '-isv'
let s:PASTE_FIXED_ARGUMENTS = '-d "description=Pastebin%20plugin%20for%20vim%20post"'
let s:PASTE_FORMAT          = '-d "fileExt=textFormat"'
let s:PASTE_NICK            = '-d "name=nickID"'
let s:PASTE_PAYLOAD         = '-d "@-"'
let s:PASTE_RESPONSE_FLAG   = "^X-Paste-Identifier"
"  
"                                  " vim-filetype   pastebin-format
let s:PASTE_SYNTAX_OPTIONS  = {   'awk':         'awk',
                                \ 'sh':          'sh',
                                \ 'c':           'c',
                                \ 'cpp':         'cpp',
                                \ 'html':        'html',
                                \ 'java':        'java',
                                \ 'javascript':  'js',
                                \ 'perl':        'pl',
                                \ 'php':         'php',
                                \ 'python':      'py',
                                \ 'ruby':        'rb',
                                \ 'default':     'text' }

let s:PASTE_TEXT_AREA       = 'content'
let s:PASTE_URI             = 'http://mysticpaste.com/servlet/plugin'
" 
function! s:ParseLocationFrom(line)
  let tokens = split(a:line, "[ \r]")
  
  return "Location: http://mysticpaste.com/view/" . tokens[0x01]
endfunction

" *************** End MysticPaste.com configuration ************


" Exit early if curl isn't available.

if !executable(s:PASTE_COMMAND)
  finish
endif


" *** Utility functions ***

let s:reservedCharacters = [ '$', '&', '+', ',', '/', ':', ';', '=', '?', '@' ]
let s:unsafeCharacters   = [ '"', '<', '>', '#', '%', '{', '}', '|', '\', '^', '~', '[', ']', '`' ]


function! s:CharEncodeURL(aSymbol)
  let nSymbol = char2nr(a:aSymbol)

  " Non-printable characters first:
  if nSymbol <= 0x20 || nSymbol > 0x7f
    return printf("%%%02X", nSymbol)
  endif

  " Characters with special meaning to URL processors:
  if index(s:reservedCharacters, a:aSymbol) > -1 || index(s:unsafeCharacters, a:aSymbol) > -1
    return printf("%%%02X", nSymbol)
  endif

  return a:aSymbol
endfunction


function! s:StringEncodeURL(text)
  let allChars = split(a:text, '\zs')
  let retVal   = ""

  for cSymbol in allChars
    let retVal .= s:CharEncodeURL(cSymbol)
  endfor

  return retVal
endfunction


function! s:BuildCacheFrom(textAsList)
  " Experimental - this may not remain here for long.
  let fileName = tempname()

  call writefile(a:textAsList, fileName)

  return fileName
endfunction


function! s:ResolveTextFormat()
  return has_key(s:PASTE_SYNTAX_OPTIONS, &filetype)
           \ ? s:PASTE_SYNTAX_OPTIONS[&filetype]
           \ : s:PASTE_SYNTAX_OPTIONS['default']
endfunction


" For OS X users:

function! s:Paste2Clipboard(locator)
  if executable("pbcopy") && !has("gui_running")  " OS X
    call system("pbcopy", a:locator)
  endif

  if has("gui_running")
    let @+ = a:locator
  endif
endfunction


function! s:ExecutePaste(text)
  let command  = s:PASTE_COMMAND
  let command .= " ".s:PASTE_CONTROL
  let command .= " ".s:PASTE_FIXED_ARGUMENTS
  let command .= " ".s:PASTE_PAYLOAD
  let command .= " ".substitute(s:PASTE_NICK, "nickID", g:nickID, "g")
  let command .= " ".substitute(s:PASTE_FORMAT, "textFormat", s:ResolveTextFormat(), "g")
  let command .= " ".s:PASTE_URI

  let output   = split(system(command, s:PASTE_TEXT_AREA.'='.a:text), '\n')
  
  redraw

  for line in output
    let nPtr = match(line, s:PASTE_RESPONSE_FLAG)

    if nPtr != -1
      let location = s:ParseLocationFrom(line)
      echomsg location

      let clipboardURL = split(location, " ")
      call s:Paste2Clipboard(clipboardURL[0x01])

      return
    endif
  endfor

  echohl ErrorMsg
  echomsg "Paste failed!"
  echohl None
endfunction


" *** Main script function ***

function! Pastebin() range
  if (!exists('g:nickID'))
    let g:nickID = inputdialog("Enter your /nick or ID for this posting: ", "Anonymous")
  endif

  call s:ExecutePaste(s:StringEncodeURL(join(getline(a:firstline, a:lastline), "\n")))
endfunction


" Command to call the function:

com! -range=% -nargs=0 Pastebin :<line1>,<line2>call Pastebin()

