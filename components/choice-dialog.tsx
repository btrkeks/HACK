import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { MessageSquare, Phone } from "lucide-react"
import { useRouter } from "next/navigation"
import { useState, useEffect } from "react"
import Input from "@/components/ui/input"

interface CompanyData {
  companyName: string | null;
  numberOfEmployees: number | null;
}

interface ChoiceDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  companyData: CompanyData | null
  onCompanyDataChange: (data: CompanyData | null) => void
}

export default function ChoiceDialog({ open, onOpenChange, companyData, onCompanyDataChange }: ChoiceDialogProps) {
  const router = useRouter()
  const [localCompanyData, setLocalCompanyData] = useState<CompanyData>({
    companyName: '',
    numberOfEmployees: null
  })

  useEffect(() => {
    if (companyData) {
      setLocalCompanyData({
        companyName: companyData.companyName || '',
        numberOfEmployees: companyData.numberOfEmployees
      })
    }
  }, [companyData])

  const handleChatClick = () => {
    onCompanyDataChange(localCompanyData)
    router.push("/chat")
    onOpenChange(false)
  }

  const handleCallClick = () => {
    onCompanyDataChange(localCompanyData)
    router.push("/call")
    onOpenChange(false)
  }

  const handleInputChange = (field: keyof CompanyData, value: string) => {
    setLocalCompanyData(prev => ({
      ...prev,
      [field]: field === 'numberOfEmployees' ? (value ? parseInt(value, 10) || null : null) : value
    }))
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[550px] p-6">
        <DialogHeader>
          <DialogTitle className="text-center text-primary text-2xl mb-2">
            Firmendaten bestätigen
          </DialogTitle>
        </DialogHeader>
        
        <div className="space-y-4 my-4">
          <div className="space-y-2">
            <label htmlFor="companyName" className="text-sm font-medium">Firmenname</label>
            <Input
              id="companyName"
              value={localCompanyData.companyName || ''}
              onChange={(e) => handleInputChange('companyName', e.target.value)}
              placeholder="Firmenname"
              className="w-full"
            />
          </div>
          
          <div className="space-y-2">
            <label htmlFor="numberOfEmployees" className="text-sm font-medium">Anzahl Mitarbeiter</label>
            <Input
              id="numberOfEmployees"
              type="number"
              value={localCompanyData.numberOfEmployees || ''}
              onChange={(e) => handleInputChange('numberOfEmployees', e.target.value)}
              placeholder="Anzahl Mitarbeiter"
              className="w-full"
            />
          </div>
        </div>
        
        <div className="grid grid-cols-2 gap-6 mt-4">
          <button
            onClick={handleChatClick}
            className="flex flex-col items-center p-8 space-y-6 bg-white border border-primary/20 rounded-lg hover:bg-primary/5 transition-colors"
          >
            <MessageSquare className="h-16 w-16 text-primary" />
            <span className="text-center font-medium text-lg">Mit KI Berater Chatten</span>
          </button>
          <button
            onClick={handleCallClick}
            className="flex flex-col items-center p-8 space-y-6 bg-white border border-primary/20 rounded-lg hover:bg-primary/5 transition-colors"
          >
            <Phone className="h-16 w-16 text-primary" />
            <span className="text-center font-medium text-lg">Mit KI Berater telefonieren</span>
          </button>
        </div>
      </DialogContent>
    </Dialog>
  )
} 