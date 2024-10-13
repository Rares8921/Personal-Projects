.data
	putereCurenta: .long 7
	# Array-urile pt directii
	di: .long -1, 0, 1, 0, -1, -1, 1, 1
	dj: .long 0, 1, 0, -1, -1, 1, 1, -1
	# Rezulta in eroare daca le scriu pe toate pe acelasi rand, deci o sa le scriu pe randuri diferite
	stringuriBinare: .long char0 
					 .long char1
					 .long char2
					 .long char3
					 .long char4
					 .long char5
					 .long char6
					 .long char7
					 .long char8
					 .long char9
					 .long charA
					 .long charB
					 .long charC
					 .long charD
					 .long charE
					 .long charF
	codAsciiCurent: .space 4
	lungimeCheie: .space 4
	lungimeStringInput: .space 4
	lungimeStringBinar: .space 4
	lungimeStringBinarDinHexa: .space 4
	lungimeStringAns: .space 4
	caracterCurent: .space 4 # Pentru parcurgerea unui string
    nrLinii: .space 4 #Numarul de linii
    nrColoane: .space 4 #Numarul de coloane
    nrCeluleVii: .space 4 #Numarul de celule vii
    x: .space 4 #Index-ul de linie din input
    y: .space 4 #Index-ul de coloana din input
    k: .space 4 #Numarul de evolutii
	cerinta: .space 4	
	# Variabilele folosite pentru a simula for-urile
	evolutiaCurenta: .space 4
	linieCurenta: .space 4
	coloanaCurenta: .space 4
	directiaCurenta: .space 4
	caracterulCurent: .space 4
	caracterulCheii: .space 4
	pozitiaCurentaSecv: .space 4
	pasCurent: .space 4
	caractereFolosite: .space 4
	# Indexii vecinilor 
	linieVecinCurent: .space 4
	coloanaVecinCurent: .space 4
	nrVeciniVii: .space 4
	nrDirectii: .space 4 # Pt vectorii de directii
	dimMax: .space 4 # Matricea extinsa va avea maxim 20 de coloane ( n <= 18, iar n + 2 <= 20 )
	secventaCurentaDeBiti: .space 12 # Daca pun doar 8 bytes, sare la adresa lui inputString, iar cu 9 bytes nu este buna alinierea in memorie
	copyString: .space 40 # Pentru push_front
	tempString: .space 40 # Pentru push_front
	inputString: .space 40 # Cred ca mergea si 20 actually
	stringBinarTemp: .space 120 # Si aici doar 80
	stringAns: .space 120 # Folosesc aceeasi variabila de rasp la ambele cerinte
	matrCelule: .space 1600 # Matricea din input, care va fi folosita cu indexi de la 1
	matrCopie: .space 1600 # Trebuie o copie a matricii la fiecare evolutie
	# Cheia creata dupa evolutii va fi chiar matrCelule
	# Pentru transformarea in binar
	pushChar0: .asciz "0"
	pushChar1: .asciz "1"
	# Pentru input
	formatIntregScanf: .asciz "%d"
	formatStringScanf: .asciz "%s"
	# Pentru output
	formatStringPrintf: .asciz "%s"
	formatCharPrintf: .asciz "%c"
	newLine: .asciz "\n"
	hexaOutput: .asciz "0x"
	# String-urile pentru transformarea din hexa in binar, pentru decriptare
	# Pentru a crea un array din secventele binare, voi trimite adresele in sir
	# Si dupa voi accesa adresa fiecarui element prin indexii array-ului
	char0: .asciz "0000"
	char1: .asciz "0001"
	char2: .asciz "0010"
	char3: .asciz "0011"
	char4: .asciz "0100"
	char5: .asciz "0101"
	char6: .asciz "0110"
	char7: .asciz "0111"
	char8: .asciz "1000"
	char9: .asciz "1001"
	charA: .asciz "1010"
	charB: .asciz "1011"
	charC: .asciz "1100"
	charD: .asciz "1101"
	charE: .asciz "1110"
	charF: .asciz "1111"
	caractereHexa: .asciz "0123456789ABCDEF"
	# String-urile care trebuiesc create sunt declarate la final din cauza aliniamentului in memorie
	stringBinar: .asciz "" # Pentru criptare
	stringBinarDinHexa: .asciz "" # Pentru decriptare

.text 
	# Simulez un for in care parcurg string-ul ca un sir de caractere si cresc nr de pasi executati
	# Voi returna prin intermediul lui %eax lungimea string-ului
	lungimeString:
		xorl %eax, %eax
		parcurgereCaracter:
			# Am incarcat in %edi string-ul inainte de apel
			mov (%edi, %eax, 1), %ebx
			cmp $0, %ebx
			je innerParcurgereCaracter
			inc %eax
			jmp parcurgereCaracter
		innerParcurgereCaracter:
			ret # Sfarsitul procedurii
		
.global main
main:

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
    push $formatIntregScanf
    call scanf
    popl %ebx
    popl %ebx
	
	#Citesc nrColoane
	pushl $nrColoane
    push $formatIntregScanf
    call scanf
    popl %ebx
    popl %ebx
	
	#Citesc nrCeluleVii
	pushl $nrCeluleVii
    push $formatIntregScanf
    call scanf
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
    push $formatIntregScanf
    call scanf
    popl %ebx
    popl %ebx

    #Citesc indexul de coloana
    pushl $y
    push $formatIntregScanf
    call scanf
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
    push $formatIntregScanf
    call scanf
    popl %ebx
    popl %ebx

citireCerinta:
	
    #Citesc cerinta
    pushl $cerinta
    push $formatIntregScanf
    call scanf
    popl %ebx
    popl %ebx

citireString:

	# Daca este vorba de decriptare, voi parcurge string-ul de la 2
	push $inputString
	push $formatStringScanf
	call scanf
	popl %ebx
	popl %ebx
	
	# Counter pt evolutii:
	movl $0, evolutiaCurenta
	
evolutii:
	movl evolutiaCurenta, %ecx
	cmp k, %ecx
	jge calculLungimeCheie 
	
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

calculLungimeCheie:
	# Calculez lungimea cheii: (nrLinii + 2) * (nrColoane + 2)
	movl nrLinii, %eax
	movl $2, %ecx 
	addl %ecx, %eax
	movl nrColoane, %ebx
	addl %ecx, %ebx
	xorl %edx, %edx # Pt inmultire
	mull %ebx
	movl %eax, lungimeCheie
	
verificareCerinta:
	lea inputString, %edi # String-ul care trebuit 
	call lungimeString
	movl %eax, lungimeStringInput

	movl $0, lungimeStringAns
	movl cerinta, %ecx
	cmp $0, %ecx
	je criptare # Daca este prima cerinta
	jne decriptare

criptare:	
	
	# Calculez lungimea string-ului binar: lungimeStringInput * 8
	calculLungimeStringBinar:
		movl $8, lungimeStringBinar
		movl lungimeStringBinar, %eax
		xorl %edx, %edx
		movl lungimeStringInput, %ecx
		mull %ecx
		movl %eax, lungimeStringBinar
	
	# Pasii criptarii: transform string-ul citit in binar pe 8 biti, apoi in functie de lungimea sirului binar
	# si de lungimea cheii voi face xor-ul celor doua array-uri. La afisare voi grupa bitii cate 4 si voi calcula
	# valoarea in decimal a secventei binare si extrag dintr-un sir de caractere hexa simbolul corespunzator 
	# numarului rezultat dupa calcule.
	
	movl $0, caracterulCurent
	transformareInBinar:
		movl caracterulCurent, %ecx
		cmp lungimeStringInput, %ecx
		je inversareSecvente
		
		# Codurile ascii ale cifrelor sunt pe 6 biti, iar literele sunt pe 7 biti
		prelucrareCaracter:
			# Extrag byte-ul curent din asciz
			# <=> codul ascii al caracterului curent din string
			movl caracterulCurent, %edi
			xorl %eax, %eax
			# Din cauza ca trebuie sa folosesc un registru pe 8 biti pt movb
			# Ma voi folosi de %al(pun al pentru ca in ah stocheaza bitii mai "sus" si "formeaza" alt nr mai mare) pentru a stoca bitul,
			# dar dupa tot cu %eax verific
			movb inputString(%edi), %al
			movl %eax, codAsciiCurent
			
			# Codul ascii pt '0' este 48, iar codul ascii pt 'A' este 65
			# Deci pentru '9' este 57 si pentru 'F' este 70	
			
			transformareCodAscii:
				movl codAsciiCurent, %ecx
				cmp $0, %ecx
				je adaugareBitiInPlus
				
				# Vad daca restul impartirii la 2 este 0 sau 1 si adaug bit-ul corespunzator
				# %edx a fost afectat de apelul cu strcat, deci il resetez
				xorl %edx, %edx
				movl codAsciiCurent, %eax
				movl $2, %ecx
				divl %ecx
				
				cmp $0, %edx
				je adaugaBitDe0
				jne adaugaBitSetat
				
				adaugaBitSetat:
					push $pushChar1
					push $stringBinarTemp
					call strcat
					popl %ebx
					popl %ebx
					jmp innerTransformareCodAscii
				
				adaugaBitDe0:
					push $pushChar0
					push $stringBinarTemp
					call strcat
					popl %ebx
					popl %ebx
				
				innerTransformareCodAscii:
					# codAsciiCurent >>= 1;
					movl codAsciiCurent, %eax
					shr $1, %eax
					movl %eax, codAsciiCurent
					jmp transformareCodAscii
	
		adaugareBitiInPlus:
				movl caracterulCurent, %edi
				xorl %eax, %eax
				movb inputString(%edi), %al
				cmp $57, %eax
				jle adaugare2Biti
				jg adaugare1Bit
			
				adaugare2Biti:
					push $pushChar0
					push $stringBinarTemp
					call strcat
					popl %ebx
					popl %ebx
					
					push $pushChar0
					push $stringBinarTemp
					call strcat
					popl %ebx
					popl %ebx
					
					jmp innerTransformareInBinar
				
				adaugare1Bit:
					push $pushChar0
					push $stringBinarTemp
					call strcat
					popl %ebx
					popl %ebx
	
		innerTransformareInBinar:
			incl caracterulCurent
			jmp transformareInBinar
	
	# In acest moment am string-ul din input transformat in binar, iar fiecare secventa de 8 biti este inversata
	
	inversareSecvente:
		movl $0, caracterulCurent
		movl $1, pasCurent
		movl $0, caractereFolosite
		movl $7, caracterCurent # Parcurg invers secventa de 8 biti
		loopInversare:
			movl caracterulCurent, %ecx
			cmp lungimeStringBinar, %ecx
			je modificareSirBinar
			
			movl caracterCurent, %edi
			xorl %eax, %eax
			movb stringBinarTemp(%edi), %al
			
			cmp $48, %eax
			je adauga0
			jne adauga1
			
			adauga0:
				push $pushChar0
				jmp adaugareCaracter
			
			adauga1:
				push $pushChar1
			
			adaugareCaracter:
				push $stringBinar
				call strcat
				popl %ebx
				popl %ebx
			
			incl caractereFolosite
			movl caractereFolosite, %ecx
			cmp $8, %ecx
			je secventaUrmatoare
			jne continuareSecventa
			
			secventaUrmatoare:
				movl $0, caractereFolosite
				incl pasCurent
				# caracterCurent = pasCurent * 8 - 1
				movl pasCurent, %eax
				shl $3, %eax
				decl %eax
				movl %eax, caracterCurent
				jmp innerLoopInversare
				
			continuareSecventa:
				decl caracterCurent
							
			innerLoopInversare:
				incl caracterulCurent
				jmp loopInversare
	
	modificareSirBinar:

		# Cresc indexii la nrLinii + 2, nrColoane + 2 pt obtinerea cheii
		incl nrLinii
		incl nrLinii
		incl nrColoane
		incl nrColoane
	
		movl lungimeCheie, %ecx
		cmp lungimeStringBinar, %ecx
		jge xorCheie
		jl xorSir
		
		xorCheie:
			movl $0, caracterulCurent
			movl $0, linieCurenta
			movl $0, coloanaCurenta
			transfDupaCheie:
				movl caracterulCurent, %ecx
				cmp lungimeStringBinar, %ecx
				je afisareStringCriptat
				
				movl caracterulCurent, %edi
				xorl %eax, %eax
				movb stringBinar(%edi), %al
					
				movl $48, %ecx
				subl %ecx, %eax
				movl %eax, %ebx
					
				# Extrag caracterul curent din cheie
				movl linieCurenta, %eax
				xorl %edx, %edx
				movl dimMax, %ecx
				mull %ecx
				addl coloanaCurenta, %eax
				lea matrCelule, %edi
				movl (%edi, %eax, 4), %ecx
					
				xorl %ebx, %ecx
				
				# Adaug valoarea in string-ul binar
				cmpl $0, %ecx
				je pBack0 # pushBack0, dar am folosit mai jos eticheta
				jne pBack1
				
				pBack0:
					push $pushChar0
					jmp addInString
				
				pBack1:
					push $pushChar1
				
				addInString:
					push $stringAns
					call strcat
					popl %ebx
					popl %ebx
				
				incl lungimeStringAns
				incl caracterulCurent
				incl caracterulCheii
				incl coloanaCurenta
					
				movl coloanaCurenta, %ecx
				cmp nrColoane, %ecx
				je incrementareLCheie1
				jne continuareTransformare1
						
				incrementareLCheie1:
					movl $0, coloanaCurenta
					incl linieCurenta
						
				continuareTransformare1:
					jmp transfDupaCheie
			
		xorSir:
			movl $0, caracterulCurent
			movl $0, caracterulCheii
			movl $0, linieCurenta
			movl $0, coloanaCurenta
			transfDupaSir:
				movl caracterulCurent, %ecx
				cmp lungimeStringBinar, %ecx
				je afisareStringCriptat
				
				# Resetare caracter cheie si variabilele de parcurgere
				movl caracterulCheii, %ecx
				cmp lungimeCheie, %ecx
				je resetCaracter
				jne innerTransfDupaSir
				
				resetCaracter:
					# Practic parcurg matricea matrCelule pentru a avea cheia
					incl linieCurenta
					movl $0, coloanaCurenta
					movl $0, linieCurenta
					movl $0, caracterulCheii
				
				innerTransfDupaSir:
					# Extrag codul ascii al caracterului curent din string-ul binar
					movl caracterulCurent, %edi
					xorl %eax, %eax
					movb stringBinar(%edi), %al
					
					movl $48, %ecx
					subl %ecx, %eax
					movl %eax, %ebx
					
					# Extrag caracterul curent din cheie
					movl linieCurenta, %eax
					xorl %edx, %edx
					movl dimMax, %ecx
					mull %ecx
					addl coloanaCurenta, %eax
					lea matrCelule, %edi
					movl (%edi, %eax, 4), %ecx
					
					xorl %ebx, %ecx
				
					# Adaug valoarea in string-ul binar
					cmpl $0, %ecx
					je pBackCu0
					jne pBackCu1
					
					pBackCu0:
						push $pushChar0
						jmp addInStr
					
					pBackCu1:
						push $pushChar1
					
					addInStr:
						push $stringAns
						call strcat
						popl %ebx
						popl %ebx
				
					incl lungimeStringAns
					incl caracterulCurent
					incl caracterulCheii
					incl coloanaCurenta
					
					movl coloanaCurenta, %ecx
					cmp nrColoane, %ecx
					je incrementareLCheie2
					jne continuareTransformare2
						
					incrementareLCheie2:
						movl $0, coloanaCurenta
						incl linieCurenta
						
					continuareTransformare2:
						jmp transfDupaSir

decriptare:
	# Transform cuvantul din hexa in binar
	movl $0, lungimeStringBinarDinHexa
	# Parcurg string-ul de la 2 ca sa sar peste "0x"
	movl $2, caracterulCurent
	parcurgereString:	# Determin lungimea string-ului din input
		movl caracterulCurent, %ecx
		cmpl lungimeStringInput, %ecx
		je modificareSirBinarDinHexa

		# Extrag codul ascii al caracterului curent
		movl caracterulCurent, %edi
		xorl %eax, %eax
		movb inputString(%edi), %al
		
		# Codul ascii pt '0' este 48, iar codul ascii pt 'A' este 65
		# Deci pentru '9' este 57 si pentru 'F' este 70
		# Scad 48 sau 55(65-10 pt ca 'A' este a 10-a cifra in baza 16), in functie de caz
		# din %ah pt verificari mai usoare si pt a gasi valoarea in array
		movl $57, %ecx
		cmp %ecx, %eax # Verific daca caracterul curent este cifra
		jbe scadereCifraAscii
		jg scadereLiteraAscii
		
		scadereCifraAscii:
			movl $48, %ecx
			sub %ecx, %eax
			jmp innerParcurgereString
			
		scadereLiteraAscii:
			movl $55, %ecx
			sub %ecx, %eax
		
		innerParcurgereString:
			#Extrag acum cei 4 biti corespunzatori valorii calculate
			lea stringuriBinare, %edi
			mov (%edi, %eax, 4), %ecx
			
			push %ecx
			push $stringBinarDinHexa
			call strcat
			popl %ebx
			popl %ebx
			
			# Cresc lungimea cu 4
			movl lungimeStringBinarDinHexa, %ecx
			movl $4, %eax
			addl %eax, %ecx 
			movl %ecx, lungimeStringBinarDinHexa
			
			incl caracterulCurent
			jmp parcurgereString
			
	# Urmeaza partea de xor a sirului cu cheia
	modificareSirBinarDinHexa:
	
		# Cresc indexii la nrLinii + 2, nrColoane + 2 pt obtinerea cheii
		incl nrLinii
		incl nrLinii
		incl nrColoane
		incl nrColoane
	
		movl lungimeCheie, %ecx
		cmp lungimeStringBinarDinHexa, %ecx
		jge xorDupaCheie
		jl xorDupaSir
		
		xorDupaCheie:
			movl $0, caracterulCurent
			movl $0, linieCurenta
			movl $0, coloanaCurenta
			transformareDupaCheie:
				movl caracterulCurent, %ecx
				cmp lungimeStringBinarDinHexa, %ecx
				je afisareStringDecriptat
				
				movl caracterulCurent, %edi
				xorl %eax, %eax
				movb stringBinarDinHexa(%edi), %al
					
				movl $48, %ecx
				subl %ecx, %eax
				movl %eax, %ebx
					
				# Extrag caracterul curent din cheie
				movl linieCurenta, %eax
				xorl %edx, %edx
				movl dimMax, %ecx
				mull %ecx
				addl coloanaCurenta, %eax
				lea matrCelule, %edi
				movl (%edi, %eax, 4), %ecx
					
				xorl %ebx, %ecx
				
				# Adaug valoarea in string-ul binar
				cmpl $0, %ecx
				je pushBack0
				jne pushBack1
				
				pushBack0:
					push $pushChar0
					jmp adaugaInString
				
				pushBack1:
					push $pushChar1
				
				adaugaInString:
					push $stringAns
					call strcat
					popl %ebx
					popl %ebx
				
				incl lungimeStringAns
				incl caracterulCurent
				incl caracterulCheii
				incl coloanaCurenta
					
				movl coloanaCurenta, %ecx
				cmp nrColoane, %ecx
				je incrementLCheie1
				jne continuareTransf1
						
				incrementLCheie1:
					movl $0, coloanaCurenta
					incl linieCurenta
						
				continuareTransf1:
					jmp transformareDupaCheie
			
		xorDupaSir:
			movl $0, caracterulCurent
			movl $0, caracterulCheii
			movl $0, linieCurenta
			movl $0, coloanaCurenta
			transformareDupaSir:
				movl caracterulCurent, %ecx
				cmp lungimeStringBinarDinHexa, %ecx
				je afisareStringDecriptat
				
				# Resetare caracter cheie si variabilele de parcurgere
				movl caracterulCheii, %ecx
				cmp lungimeCheie, %ecx
				je resetareCaracter
				jne innerTransformareDupaSir
				
				resetareCaracter:
					# Practic parcurg matricea matrCelule pentru a avea cheia
					incl linieCurenta
					movl $0, coloanaCurenta
					movl $0, linieCurenta
					movl $0, caracterulCheii
				
				innerTransformareDupaSir:
					# Extrag codul ascii al caracterului curent din string-ul binar
					movl caracterulCurent, %edi
					xorl %eax, %eax
					movb stringBinarDinHexa(%edi), %al
					
					movl $48, %ecx
					subl %ecx, %eax
					movl %eax, %ebx
					
					# Extrag caracterul curent din cheie
					movl linieCurenta, %eax
					xorl %edx, %edx
					movl dimMax, %ecx
					mull %ecx
					addl coloanaCurenta, %eax
					lea matrCelule, %edi
					movl (%edi, %eax, 4), %ecx
					
					xorl %ebx, %ecx
				
					# Adaug valoarea in string-ul binar
					cmpl $0, %ecx
					je pushBackCu0
					jne pushBackCu1
					
					pushBackCu0:
						push $pushChar0
						jmp adaugaInStr
					
					pushBackCu1:
						push $pushChar1
					
					adaugaInStr:
						push $stringAns
						call strcat
						popl %ebx
						popl %ebx
				
					incl lungimeStringAns
					incl caracterulCurent
					incl caracterulCheii
					incl coloanaCurenta
					
					movl coloanaCurenta, %ecx
					cmp nrColoane, %ecx
					je incrementLCheie2
					jne continuareTransf2
						
					incrementLCheie2:
						movl $0, coloanaCurenta
						incl linieCurenta
						
					continuareTransf2:
						jmp transformareDupaSir
		
afisareStringCriptat:

	push $hexaOutput
	push $formatStringPrintf
	call printf
	popl %ebx
	popl %ebx
	
	pushl $0
	call fflush
	popl %ebx
	
	movl $0, caracterulCurent
	movl $0, linieCurenta # Valoarea curenta a secventei de 4 biti ca intreg
	movl $3, putereCurenta
	afisareHexadecimal:
		movl caracterulCurent, %ecx
		cmp lungimeStringBinar, %ecx
		je exit
		
		# Calculez valoarea intreaga a secventei
		movl caracterulCurent, %edi
		xorl %eax, %eax
		movb stringAns(%edi), %al
		
		cmp $49, %eax # Daca este bit setat
		je adaugareLaValoare
		jne verificareSecventa
		
		adaugareLaValoare:
			movl $1, %eax
			movl $0, coloanaCurenta
			inmultireCuDoi:
				movl coloanaCurenta, %ecx
				cmp putereCurenta, %ecx
				je innerAdaugareLaValoare
				
				shl $1, %eax
				
				incl coloanaCurenta
				jmp inmultireCuDoi
				
			innerAdaugareLaValoare:
				movl linieCurenta, %ecx
				addl %ecx, %eax
				movl %eax, linieCurenta
		
		verificareSecventa:
			# Verific daca s-a facut o secventa de 4 biti, apoi afisez caracterul hexa corespunzator
			movl caracterulCurent, %eax 
			incl %eax
			xorl %edx, %edx
			movl $4, %ecx
			divl %ecx
		
			cmp $0, %edx # S-a terminat secventa, afisez caracterul
			je afisareCaracter
			jne scaderePutere
		
		afisareCaracter:
			movl linieCurenta, %edi
			xorl %eax, %eax
			movb caractereHexa(%edi), %al
			
			push %eax
			push $formatCharPrintf
			call printf 
			popl %ebx
			popl %ebx
			
			push $0
			call fflush
			popl %ebx
			
			movl $3, putereCurenta
			movl $0, linieCurenta
			
			jmp innerAfisareHexadecimal
		
		scaderePutere:
			decl putereCurenta
		
		innerAfisareHexadecimal:
			incl caracterulCurent
			jmp afisareHexadecimal
		
afisareStringDecriptat:
	# Acum trebuie sa grupez caracterele cate 8 si transform binarul in cod ascii
	# Si afisez caracterul corespunzator codului ascii
	# Afisez elementul
	
	movl $0, caracterulCurent
	movl $0, caracterCurent
	xorl %edx, %edx
	
	transformare:
	
		movl caracterulCurent, %ecx
		cmp lungimeStringAns, %ecx
		jg exit
	
		movl $8, %ecx
		cmp caracterCurent, %ecx
		je afisareCodAscii
		jne innerTransformare
		
		afisareCodAscii:
			movl $0, caracterCurent
			# In %ecx voi avea indicele de start al secventei de 8 caractere din string
			movl caracterulCurent, %ecx
			movl $8, %eax
			subl %eax, %ecx	
			
			# Folosesc "rep", o instructiune de tipul repeat string operation, iar rep inseamna repeat while equal
			# Prin rep se executa instructiuni pana cand registrul %cx ajunge la o valoare anume
			# Practic simulez un for cu 8 pasi in care copiez bitii curenti
			# Folosesc movsb pentru a copia octetii de la sursa la destinatie, de aceea folosesc esi si edi
			# source index si destination index
			lea stringAns, %esi      
			# Adaug pozitia de pe care trebuie sa inceapa secventa, adica caracterulCurent - 7
			addl %ecx, %esi
			lea secventaCurentaDeBiti, %edi
			mov $8, %ecx
			rep movsb
			
			movl $7, putereCurenta
			movl $0, codAsciiCurent
			movl $0, pozitiaCurentaSecv
			
			binarInText:
				movl pozitiaCurentaSecv, %ecx
				cmp $8, %ecx
				je innerAfisareCodAscii
				
				movl pozitiaCurentaSecv, %ecx
				xorl %eax, %eax
				movb secventaCurentaDeBiti(%ecx), %al
				
				cmp $48, %eax
				je innerBinarInText
				
				# Altfel adaug la rezultat
				
				movl $0, coloanaCurenta
				movl $1, %eax
				inmultireCu2:
					movl coloanaCurenta, %ebx
					cmp putereCurenta, %ebx
					je calculareCodAscii
					
					shl $1, %eax
					
					incl coloanaCurenta
					jmp inmultireCu2
				
				calculareCodAscii:
					movl codAsciiCurent, %ebx
					addl %eax, %ebx
					movl %ebx, codAsciiCurent
				
				innerBinarInText:
					decl putereCurenta
					incl pozitiaCurentaSecv
					jmp binarInText
			
			innerAfisareCodAscii:
			
				pushl codAsciiCurent
				pushl $formatCharPrintf
				call printf
				popl %ebx
				popl %ebx
					
				pushl $0	
				call fflush
				popl %ebx
			
				# Instructiunea stosb inseamna stocarea lui %al la adresa %edi
				# "Resetez" secventa din %edi, adica secventaCurentaDeBiti
				# Ce se intampla de fapt e ca se copiaza byte-urile din %al in adresa de memorie de la %edi,
				# dar %al va fi empty
				xorl %eax, %eax
				rep stosb
			
		innerTransformare:
			incl caracterCurent
			incl caracterulCurent
			jmp transformare

exit:
	
	pushl $newLine
	pushl $formatStringPrintf
	call printf
	popl %ebx
	popl %ebx
					
	pushl $0	
	call fflush
	popl %ebx

    movl $1, %eax
    xorl %ebx, %ebx
    int $0x80
