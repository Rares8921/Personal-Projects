.data
	# Array-urile pt directii
	di: .long -1, 0, 1, 0, -1, -1, 1, 1
	dj: .long 0, 1, 0, -1, -1, 1, 1, -1
    nrLinii: .space 4 #Numarul de linii
    nrColoane: .space 4 #Numarul de coloane
    nrCeluleVii: .space 4 #Numarul de celule vii
    x: .space 4 #Index-ul de linie din input
    y: .space 4 #Index-ul de coloana din input
    k: .space 4 #Numarul de evolutii
	# Variabilele folosite pentru a simula for-urile
	evolutiaCurenta: .space 4
	linieCurenta: .space 4
	coloanaCurenta: .space 4
	directiaCurenta: .space 4
	# Indexii vecinilor 
	linieVecinCurent: .space 4
	coloanaVecinCurent: .space 4
	nrVeciniVii: .space 4
	nrDirectii: .space 4 # Pt vectorii de directii
	dimMax: .space 4 # Matricea extinsa va avea maxim 20 de coloane ( n <= 18, iar n + 2 <= 20 )
	matrCelule: .space 1600 # Matricea din input, care va fi folosita cu indexi de la 1
	matrCopie: .space 1600 # Trebuie o copie a matricii la fiecare evolutie
	# Pentru fisiere
	finPtr: .long 0
	foutPtr: .long 0
	read: .asciz "r"
	write: .asciz "w"
	fin: .asciz "in.txt"
	fout: .asciz "out.txt"
	formatIntregFscanf: .asciz "%d" # Pentru input
	# Matricea va fi afisata linie cu linie, cu spatii intre elemente
	formatIntregFprintf: .asciz "%d "
	newLine: .asciz "\n"
	
.text
.global main
main:

	# Creez cele doua fisiere pentru input si output

	pushl $read
	pushl $fin
	call fopen
	popl %ebx
	popl %ebx
	movl %eax, finPtr
	
	pushl $write
	pushl $fout
	call fopen
	popl %ebx
	popl %ebx
	movl %eax, foutPtr
	
	#Daca trb apelata func(ag1, ag2, .., ag(n - 1), agn), atunci:
	# push $agn
	# push $ag(n-1)
	# ...
	# pushl $ag1
	# call func
	# popl %ebx
	# ...

	#Citesc nrLinii
    pushl $nrLinii
    push $formatIntregFscanf
	push finPtr
    call fscanf
    popl %ebx
    popl %ebx
	popl %ebx
	
	#Citesc nrColoane
	pushl $nrColoane
    push $formatIntregFscanf
	push finPtr
    call fscanf
    popl %ebx
    popl %ebx
	popl %ebx
	
	#Citesc nrCeluleVii
	pushl $nrCeluleVii
    push $formatIntregFscanf
	push finPtr
    call fscanf
    popl %ebx
    popl %ebx
	popl %ebx
	
	# Declararea variabilelor
	movl $20, dimMax
	movl $8, nrDirectii
	
	# Voi folosi jge si jg in loop-uri in cazul in care la input se da n, m, p sau k <= 0
	
	# Counter pt initializarea matricii cu 0
	movl $0, linieCurenta
	
	# In memorie, matricea este memorata ca un vector iar indicele se calculeaza cu formula:
	# linie * nrColoane + j
	# In acest caz, declar matricea initiala de 20 pe 20(din cauza necesitatii de a borda)
	# Asadar in loc de nrColoane, voi folosi 20 in calcule
	
	fillLinie:
		movl linieCurenta, %ecx
		cmp dimMax, %ecx
		jge innerMain
		movl $0, coloanaCurenta
	
			fillColoana:
			# Verific daca for-ul s-a terminat
			movl coloanaCurenta, %ecx
			cmp dimMax, %ecx
			jge innerFillLinie
			
			# Iau elementul din matrCelule[i][j] si il setez la 0
			# Adica, in cazul acesta, elem cu index-ul linieCurenta * 20 + coloanaCurenta
			movl linieCurenta, %eax
			xorl %edx, %edx
			movl dimMax, %ecx
			mull %ecx
			addl coloanaCurenta, %eax
			lea matrCelule, %edi
			movl $0, (%edi, %eax, 4)
			lea matrCopie, %edi
			movl $0, (%edi, %eax, 4)
			
			incl coloanaCurenta
			jmp fillColoana
		
		innerFillLinie:
			incl linieCurenta
			jmp fillLinie
	
	innerMain:
		# Counter pt citire
		movl $0, linieCurenta
	
citireCeluleVii:

	movl linieCurenta, %ecx
    cmp nrCeluleVii, %ecx
    jge citireK

	#Citesc indexul de linie
    pushl $x
    push $formatIntregFscanf
	push finPtr
    call fscanf
    popl %ebx
    popl %ebx
	popl %ebx

    #Citesc indexul de coloana
    pushl $y
    push $formatIntregFscanf
	push finPtr
    call fscanf
    popl %ebx
    popl %ebx
	popl %ebx

	# Indexez de la 1
	incl x
	incl y

	# Index-ul in matrice va fi x * 20 + y
	movl x, %eax
	xorl %edx, %edx # Pt inmultire
	movl dimMax, %ecx
	mull %ecx
	addl y, %eax 
	lea matrCelule, %edi
	movl $1, (%edi, %eax, 4)
	# Actualizez si copia
	lea matrCopie, %edi
	movl $1, (%edi, %eax, 4)
	
	#Continui for-ul
	incl linieCurenta
    jmp citireCeluleVii
	
citireK:
	
    #Citesc k
    pushl $k
    push $formatIntregFscanf
	push finPtr
    call fscanf
    popl %ebx
    popl %ebx
	popl %ebx
	
	# Inchid fisierul de citire
	#push $fin
	#call fclose
	#popl %ebx
	
	# Counter pt evolutii:
	movl $0, evolutiaCurenta
	
evolutii:
	movl evolutiaCurenta, %ecx
	cmp k, %ecx
	jge afisareMatrice 
	
	# Counter pt a parcurge liniile:
	movl $0, linieCurenta
	parcurgereLinii:
	
		movl linieCurenta, %ecx
		cmp nrLinii, %ecx
		jge resetVariabila # Sare la copierea matricii
	
		#Counter pt a parcurge coloanele:
		movl $0, coloanaCurenta
		parcurgereColoane:
			movl coloanaCurenta, %ecx
			cmp nrColoane, %ecx
			jge innerParcurgereLinii
			
			# Pt directii
			movl $0, directiaCurenta
			movl $0, nrVeciniVii
			# Calculam nr de vecini vii ai elementului curent
			parcurgereDirectii:
				movl directiaCurenta, %ecx
				cmp nrDirectii, %ecx
				jge innerParcurgereColoane
				
				# Elementul curent din vectorii de directii
				movl directiaCurenta, %eax
				# Calculez indicele de linie 
				lea di, %edi
				movl (%edi, %eax, 4), %edx
				addl linieCurenta, %edx
				movl %edx, linieVecinCurent
				# Calculez indicele de coloana
				lea dj, %edi
				movl (%edi, %eax, 4), %edx
				addl coloanaCurenta, %edx
				movl %edx, coloanaVecinCurent
				
				# Extrag vecinul din matrice
				# Adica, in cazul acesta, elem cu index-ul (linieVecinCurent + 2) * 20 + (coloanaVecinCurent + 2)
				movl linieVecinCurent, %eax
				incl %eax
				xorl %edx, %edx
				movl dimMax, %ecx
				mull %ecx
				addl coloanaVecinCurent, %eax
				incl %eax
				lea matrCelule, %edi
				movl (%edi, %eax, 4), %ebx
				
				# Verific daca vecinul este viu
				cmpl $1, %ebx
				je actualizareVecini
				jne innerParcurgereDirectii
				
				actualizareVecini:
					incl nrVeciniVii
				
				innerParcurgereDirectii:
					incl directiaCurenta
					jmp parcurgereDirectii
			
			# Breakpoint pentru cand se termina prelucrarea matricii
			innerParcurgereColoane:
			
				# Extrag elementul curent din matrice
				# (linieCurenta + 1) * 20 + (coloanaCurenta + 1)
				movl linieCurenta, %eax
				incl %eax
				xorl %edx, %edx
				movl dimMax, %ecx
				mull %ecx
				addl coloanaCurenta, %eax
				incl %eax
				lea matrCelule, %edi
				movl (%edi, %eax, 4), %ebx
				
				movl nrVeciniVii, %ecx # Pt verificari
				# Verific daca elementul curent este celula vie
				cmpl $1, %ebx
				je celulaVie
				jne celulaMoarta
				
				celulaVie:
					cmp $2, %ecx # Conditia de subpopulare
					jl actualizareCuZero
					
					cmp $3, %ecx # Conditia de ultrapopulare
					jg actualizareCuZero
					
					jmp actualizareCuUnu
					
					# Continuitatea celulelor vii si moarte nu necesita alte verificari
				
				celulaMoarta:
					cmp $3, %ecx # Conditia de creare
					je actualizareCuUnu
				
				# (!) Actualizez elementele in matricea copie, pentru a nu afecta nr de vecini al fiecarei celule
				actualizareCuZero:
					lea matrCopie, %edi
					movl $0, (%edi, %eax, 4)
					jmp incrementareColoana
				
				actualizareCuUnu:
					lea matrCopie, %edi
					movl $1, (%edi, %eax, 4)

			incrementareColoana:
				incl coloanaCurenta
				jmp parcurgereColoane
		
		# Breakpoint pentru cand iese din for-ul cu parcurgerea coloanelor
		innerParcurgereLinii:
			incl linieCurenta
			jmp parcurgereLinii
			
	resetVariabila:
		movl $0, linieCurenta
		# Actualizez matricea curenta cu elementele prelucrate
		copiereLinii:
			movl linieCurenta, %ecx
			cmp nrLinii, %ecx
			jge innerEvolutii
			movl $0, coloanaCurenta
			copiereColoane:
				movl coloanaCurenta, %ecx
				cmp nrColoane, %ecx
				jge innerCopiereLinii
				
				# Extrag elementul curent din matrice copie si il pun in matricea originala
				# (linieCurenta + 1) * 20 + (coloanaCurenta + 1)
				movl linieCurenta, %eax
				xorl %edx, %edx
				incl %eax
				movl dimMax, %ecx
				mull %ecx
				addl coloanaCurenta, %eax
				incl %eax
				lea matrCopie, %edi
				movl (%edi, %eax, 4), %ebx
				
				# Copiez elementul in matrice
				
				cmp $0, %ebx
				je copiereCuZero
				jne copiereCuUnu
				
				copiereCuZero:
					lea matrCelule, %edi
					movl $0, (%edi, %eax, 4)
					jmp incrementColoana
				copiereCuUnu:
					lea matrCelule, %edi
					movl $1, (%edi, %eax, 4)
				
				incrementColoana:
					incl coloanaCurenta
					jmp copiereColoane
					
			innerCopiereLinii:
				incl linieCurenta
				jmp copiereLinii
			
	# Breakpoint pentru cand se termina copierea
	innerEvolutii: 
		incl evolutiaCurenta
		jmp evolutii
	
afisareMatrice:
	movl $0, linieCurenta
	
	afisareLinii:
		movl linieCurenta, %ecx
		cmp nrLinii, %ecx
		jge exit
		movl $0, coloanaCurenta
		
		afisareColoane:
			# Verific daca for-ul s-a terminat
			movl coloanaCurenta, %ecx
			cmp nrColoane, %ecx
			jge innerAfisareLinii
			
			# Iau elementul din matrCelule[i][j], 
			# Adica, in cazul acesta, elem cu index-ul linieCurenta * nrColoane + coloanaCurenta
			movl linieCurenta, %eax
			incl %eax
			xorl %edx, %edx
			movl dimMax, %ecx
			mull %ecx
			addl coloanaCurenta, %eax
			incl %eax
			lea matrCelule, %edi
			movl (%edi, %eax, 4), %ebx
			
			# Afisez elementul
			pushl %ebx
			pushl $formatIntregFprintf
			pushl foutPtr
			call fprintf
			popl %ebx
			popl %ebx
			popl %ebx
			
			pushl foutPtr
			call fflush
			popl %ebx
			
			incl coloanaCurenta
			jmp afisareColoane
		
		innerAfisareLinii:
			
			# Trec la linie noua
			pushl $newLine
			pushl foutPtr
			call fprintf
			popl %ebx
			popl %ebx
			
			pushl foutPtr
			call fflush
			popl %ebx
			
			incl linieCurenta
			jmp afisareLinii

exit:

    pushl $0
	call fflush
	popl %ebx

    movl $1, %eax
    xorl %ebx, %ebx
    int $0x80

